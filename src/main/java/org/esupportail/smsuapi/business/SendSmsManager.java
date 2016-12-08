package org.esupportail.smsuapi.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.InvalidParameterException;
import org.esupportail.smsuapi.services.scheduler.SchedulerUtils;
import org.esupportail.smsuapi.services.sms.ISMSSender;


/**
 * Business layer concerning smsu service.
 *
 */
public class SendSmsManager {

	/**
	 * Log4j logger.
	 */
	protected final Logger logger = Logger.getLogger(getClass());

	private String phoneNumberPattern;
	
	/**
	 * {@link DaoService}.
	 */
	protected DaoService daoService;

	/**
	 * {@link ISMSSender}.
	 */
	protected ISMSSender smsSender;

	/**
	 * Used to launch task.
	 */
	protected SchedulerUtils schedulerUtils;

	/**
	 *  {@link ClientManager}.
	 */
	protected ClientManager clientManager;

	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public SendSmsManager() {
		super();
	}

	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public void checkQuotaOk(final Integer nbDest, Account account) 
	throws InsufficientQuotaException {

		Application app = clientManager.getApplication();
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
	public Account mayCreateAccountAndCheckQuotaOk(final Integer nbDest, final String labelAccount) 
	throws InsufficientQuotaException {

		Application app = clientManager.getApplication();
		{
			Account acc = labelAccount == null ? app.getAcc() :
				daoService.getAccByLabel(labelAccount);
			if (acc == null) {
				// - create new account
				acc = Account.createDefault(app, labelAccount);
				daoService.addAccount(acc);
				throw new InsufficientQuotaException("Quota error: account " + labelAccount + " has been created with quota of 0");
			}		
			checkQuotaOk(nbDest, acc);
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
			final String labelAccount, final String msgContent) throws InsufficientQuotaException {

			checkPhoneNumbers(smsPhones);
			
			List<Sms> smss = saveSMS(msgId, senderId, smsPhones, labelAccount);
			// if the client did not give a msgId, a new one has been found, take it from first smss (since all smss have the same initialId)
			msgId = smss.size() > 0 ? smss.get(0).getInitialId() : null; 

			List<SMSBroker> smsMessages = convertToSMSBroker(msgContent, smss);

			if (smsMessages != null) { 
				// launch the task witch manage the sms sending
				schedulerUtils.launchSuperviseSmsSending(smsMessages);
			}
			return msgId;
	}

	private void checkPhoneNumbers(final String[] smsPhones) {
		for (String smsPhone : smsPhones) {
			if (!smsPhone.matches(phoneNumberPattern))
				throw new InvalidParameterException("invalid phoneNumber \"" + smsPhone + "\"");
		}
	}


	private List<Sms> saveSMS(Integer msgId, final Integer senderId,
			final String[] smsPhones, 
			final String labelAccount) throws InsufficientQuotaException {

		Application app = clientManager.getApplicationOrNull();
		
		// check if the sms already exists (in case of FO problem...)
		if (msgId != null && app != null) {
			for (String smsPhone: smsPhones) {
				if (!daoService.getSms(app, msgId, smsPhone).isEmpty()) {
					String msg = "SMS already sent! Check for a problem with the application : " + app.getName();
					logger.error(msg);
					throw new InvalidParameterException(msg);
				}
			}
		}
		
		Account account = mayCreateAccountAndCheckQuotaOk(smsPhones.length, labelAccount);
		
		List<Sms> list = new ArrayList<>();
			for (String smsPhone : smsPhones) {
				Sms sms = saveSMSNoCheck(msgId, senderId, smsPhone, account, app);
				if (msgId == null) {
					// the app did not give an "initialId", the first SMS did get a new initialId, re-use it for the others sms
					msgId = sms.getInitialId();
				}
				list.add(sms);
			}
		return list;
	}

	private List<SMSBroker> convertToSMSBroker(final String msgContent, List<Sms> smss) {
		List<SMSBroker> list = new ArrayList<>();
		for (Sms sms : smss) {
			if (sms.getStateAsEnum().equals(SmsStatus.IN_PROGRESS)) {
				list.add(new SMSBroker(sms.getId(), sms.getPhone(), msgContent, sms.getAcc().getLabel()));
			}
		}
		return list.size() > 0 ? list : null;
	}

	protected Sms saveSMSNoCheck(final Integer msgId, final Integer senderId,
			final String smsPhone, Account account, Application app) {	
		
		boolean isBlacklisted = daoService.getBlackLListByPhone(smsPhone) != 0;
		
		Sms sms = new Sms();
		sms.setAcc(account);
		sms.setApp(app);
		sms.setInitialId(msgId);
		sms.setPhone(smsPhone);
		sms.setSenderId(senderId);
		sms.setStateAsEnum(isBlacklisted ? SmsStatus.ERROR_PRE_BL : SmsStatus.IN_PROGRESS);
		sms.setDate(new Date());
		daoService.addSms(sms); // if null initialId, it will compute a new unique initialId

		if (!isBlacklisted) {
			account.setConsumedSms(account.getConsumedSms() + 1);
			daoService.updateAccount(account); 
		
			app.setConsumedSms(app.getConsumedSms() + 1);
			daoService.updateApplication(app);
		}
		return sms;		
	}

	/**
	 * Used to send message in state waiting_for_sending.
	 */
	public void sendWaitingForSendingSms(final SMSBroker smsMessage) {
		smsSender.sendMessage(smsMessage);
	}
	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////

	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	public void setSmsSender(final ISMSSender smsSender) {
		this.smsSender = smsSender;
	}

	/**
	 * Standard setter used by spring.
	 * @param schedulerUtils
	 */
	public void setSchedulerUtils(final SchedulerUtils schedulerUtils) {
		this.schedulerUtils = schedulerUtils;
	}

	/**
	 * @param clientManager
	 */
	public void setClientManager(final ClientManager clientManager) {
		this.clientManager = clientManager;
	}

	public void setPhoneNumberPattern(String phoneNumberPattern) {
		this.phoneNumberPattern = phoneNumberPattern;
	}

}
