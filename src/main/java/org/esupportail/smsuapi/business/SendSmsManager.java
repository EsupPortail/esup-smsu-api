package org.esupportail.smsuapi.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.AlreadySentException;
import org.esupportail.smsuapi.services.scheduler.SchedulerUtils;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Business layer concerning smsu service.
 *
 */
public class SendSmsManager implements InitializingBean {

	/**
	 * Log4j logger.
	 */
	protected final Logger logger = Logger.getLogger(getClass());

	private Pattern phoneNumberPattern;
	private String defaultBroker;
	
	private Map<String, ISMSSender> smsSenders;
	@Inject private DaoService daoService;
	@Inject private SchedulerUtils schedulerUtils;
	
	private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public void checkQuotaOk(final Integer nbDest, Account account, Application app) 
	throws InsufficientQuotaException {

		{
			if (account == null) account = app.getAcc();

			if (!account.checkQuota(nbDest))
				throw new InsufficientQuotaException("Quota error: can not send " + nbDest + " sms with " + account);
			if (!app.checkQuota(nbDest))
				throw new InsufficientQuotaException("Quota error: can not send " + nbDest + " sms with " + app);
		}
	}

		/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public Account mayCreateAccountAndCheckQuotaOk(final Integer nbDest, String labelAccount, Application app) 
	throws InsufficientQuotaException {
	    logger.info("mayCreateAccountCheckQuotaOk method with parameters : " + 
				     " - nbDest = " + nbDest + 
                     " - labelAccount = " + labelAccount);
                          
		{
			Account acc = labelAccount == null ? app.getAcc() :
				daoService.getAccByLabel(labelAccount);
			if (acc == null) {
				// - create new account
				acc = Account.createDefault(app, labelAccount);
				daoService.addAccount(acc);
				throw new InsufficientQuotaException("Quota error: account " + labelAccount + " has been created with quota of 0");
			}		
			checkQuotaOk(nbDest, acc, app);
			return acc;
		}
	}

	/**
	 * @param msgId 
	 * @param senderId 
	 * @param smsPhones 
	 * @param labelAccount 
	 * @param msgContent 
	 * @throws InsufficientQuotaException 
	 */
	public Integer sendSMS(Integer msgId, final Integer senderId,
			final String[] smsPhones, 
			final String labelAccount, final String msgContent, Application app) throws InsufficientQuotaException {

                logger.info("Receive from SendSms client message : " + 
				     " - message id = " + msgId + 
				     " - sender id = " + senderId + 
				     " - recipient phone numbers = " + join(smsPhones, " ") + 
				     " - user label account = " + labelAccount + 
				     " - message = " + msgContent);


			checkAndFormatPhoneNumbers(smsPhones);
			
			List<Sms> smss = saveSMS(msgId, senderId, smsPhones, labelAccount, app);
			// if the client did not give a msgId, a new one has been found, take it from first smss (since all smss have the same initialId)
			msgId = smss.size() > 0 ? smss.get(0).getInitialId() : null; 

			SMSBroker smsMessages = convertToSMSBroker(msgContent, smss);

			if (smsMessages != null) { 
				// launch the task which manage the sms sending
				schedulerUtils.launchSuperviseSmsSending(smsMessages);
			}
			return msgId;
	}

	/**
	 * @return Format on E164 format. For example : +33606060606
	 */
	private void checkAndFormatPhoneNumbers(final String[] smsPhones) {
		for (int i = 0; i < smsPhones.length; i++) {
			String smsPhone = smsPhones[i];
			
			if (!phoneNumberPattern.matcher(smsPhone).matches()) {
				throw new InvalidParameterException("invalid phoneNumber \"" + smsPhone + "\"");
			}
			smsPhones[i] = formatPhoneNumber(smsPhone);
		}
	}
	
	private String formatPhoneNumber(String phoneNumber) {
		// Add "+" if necessary
		// (If client does not encode an international number (by replacing "+" with "%2B"), the "+" disappears)
		if (!phoneNumber.startsWith("+") && !phoneNumber.startsWith("0")) {
			logger.warn("\"" + phoneNumber + "\" not encoded correctly");
			phoneNumber = "+" + phoneNumber;
		}
		
		try {
			PhoneNumber phoneNumberObj = phoneNumberUtil.parse(phoneNumber, "FR");
			return phoneNumberUtil.format(phoneNumberObj, PhoneNumberFormat.E164);
		} catch (NumberParseException e) {
			logger.warn("invalid phoneNumber \"" + phoneNumber + "\"", e);
			return phoneNumber;
		}
	}

	private List<Sms> saveSMS(Integer msgId, final Integer senderId,
			final String[] smsPhones, 
			final String labelAccount, Application app) throws InsufficientQuotaException {

		// check if the sms already exists (in case of FO problem...)
		if (msgId != null && app != null) {
			for (String smsPhone: smsPhones) {
				if (!daoService.getSms(app, msgId, smsPhone).isEmpty()) {
					String msg = "SMS already sent! Check for a problem with the application : " + app.getName();
					logger.error(msg);
					throw new AlreadySentException(msg);
				}
			}
		}
		
		Account account = mayCreateAccountAndCheckQuotaOk(smsPhones.length, labelAccount, app);

		List<Sms> list = new ArrayList<>();

		if (msgId == null) {
                    msgId = daoService.getNewInitialId(app);
		}

			int count = 0;
			for (String smsPhone : smsPhones) {
				boolean isBlacklisted = daoService.getBlackLListByPhone(smsPhone) != 0;
				if (!isBlacklisted) count++;
				Sms sms = createSms(msgId, senderId, smsPhone, account, app, isBlacklisted);
				if (msgId == null) {
					// the app did not give an "initialId", the first SMS did get a new initialId, re-use it for the others sms
					msgId = sms.getInitialId();
				}
				list.add(sms);
			}
			daoService.addObjects(list);

			if (count > 0) {
				account.setConsumedSms(account.getConsumedSms() + count);
				daoService.updateAccount(account); 
			
				app.setConsumedSms(app.getConsumedSms() + count);
				daoService.updateApplication(app);
			}
		return list;
	}

	private SMSBroker convertToSMSBroker(final String msgContent, List<Sms> smss) {
		List<SMSBroker.Rcpt> list = new ArrayList<>();
		for (Sms sms : smss) {
			if (sms.getStateAsEnum().equals(SmsStatus.IN_PROGRESS)) {
				list.add(new SMSBroker.Rcpt(sms.getId(), sms.getPhone()));
			}
		}
		if (list.isEmpty()) return null;
		Account account = smss.get(0).getAcc();
		return new SMSBroker(list, msgContent, account);
	}

	protected Sms createSms(final Integer msgId, final Integer senderId,
			final String smsPhone, Account account, Application app, boolean isBlacklisted) {			
		Sms sms = new Sms();
		sms.setAcc(account);
		sms.setApp(app);
		sms.setInitialId(msgId);
		sms.setPhone(smsPhone);
		sms.setSenderId(senderId);
		sms.setStateAsEnum(isBlacklisted ? SmsStatus.ERROR_PRE_BL : SmsStatus.IN_PROGRESS);
		sms.setDate(new Date());
		return sms;		
	}

	/**
	 * Used to send message in state waiting_for_sending.
	 */
	public void sendWaitingForSendingSms(final SMSBroker smsMessage) {
		String broker = smsMessage.broker != null ? smsMessage.broker : defaultBroker;
		ISMSSender smsSender = smsSenders.get(broker);
		if (smsSender != null) {
		    smsSender.sendMessage(smsMessage, smsMessage.brokerLogin, smsMessage.brokerPassword);
		} else {
		    String err = smsMessage.broker == null ?
		         "unknown default broker ''" + broker + "'" :
		         "unknown broker '" + broker + "' for account '" + smsMessage.accountLabel + "'";
		    String suggestion = "uncomment <import> " + broker + ".xml in broker.xml";
		    logger.error(err + ". " + suggestion);
		}
    }

	public static String join(Object[] elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}

	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////

	public void setPhoneNumberPattern(String phoneNumberPattern) {
		this.phoneNumberPattern = Pattern.compile(phoneNumberPattern);
	}

    @Required
    public void setDefaultBroker(String defaultBroker) {
        this.defaultBroker = defaultBroker;
    }

    @Inject
    @Required
    public void setSmsSenders(List<ISMSSender> smsSenders) {
        this.smsSenders = new HashMap<>();
        for (ISMSSender smsSender : smsSenders) {
            this.smsSenders.put(smsSender.getId(), smsSender);
        }
    }

	public void afterPropertiesSet() {
        if (!this.smsSenders.containsKey(defaultBroker)) {
            throw new RuntimeException("unknonwn defaultBroker " + defaultBroker + ". Known brokers: " + this.smsSenders.keySet());
        }
    }
}
