package org.esupportail.smsuapi.business;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.services.scheduler.SchedulerUtils;
import org.esupportail.smsuapi.services.sms.ISMSSender;

/**
 * Business layer concerning smsu service.
 *
 */
public class SendSmsThirdManager {
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 * {@link ISMSSender}.
	 */
	private ISMSSender smsSender;
	
	/**
	 * Used to launch task.
	 */
	private SchedulerUtils schedulerUtils;
	
	/**
	 *  {@link ClientManager}.
	 */
	private ClientManager clientManager;
	
	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public SendSmsThirdManager() {
		super();
	}
	
	/**
	 * @throws InsufficientQuotaException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	@SuppressWarnings("unchecked")
	public void sendSMSByThird(final List<String> smsPhoneList, final String msgContent, final int msgId)
						throws UnknownIdentifierApplicationException, 
						InsufficientQuotaException {
		
		// Retrieve application by Name found in certificat
		Application app = clientManager.getApplication();
		
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else {
				String labelAccount = app.getAcc().getLabel();
				Boolean retVal = checkQuotaForThird(app, smsPhoneList.size());
				if (!retVal) {
					throw new InsufficientQuotaException("Quota error");
				} else {
					List<SMSBroker> smsMessageList = new ArrayList();
					for (String phone : smsPhoneList) {
						SMSBroker smsMessage = saveSMSByThird(app, phone, labelAccount, msgContent, msgId);
						// créer toute la liste
						if (smsMessage != null) {
							if (logger.isDebugEnabled()) {
								logger.debug("smsMessage is : " + 
									     " - smsMessage id is : " + smsMessage.getId() + 
									     " - smsMessage content is : " + smsMessage.getMessage() + 
									     " - smsMessage phone is : " + smsMessage.getRecipient());
							}
							smsMessageList.add(smsMessage);
							
						}
					}
					// launch the task witch manage the sms sending
					schedulerUtils.launchSuperviseSmsSendingByThird(smsMessageList);
				}
		}
	}
	
	/**
	 * @throws NumberPhoneInBlackListException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	private SMSBroker saveSMSByThird(final Application app, final String smsPhone, final String labelAccount, 
			final String msgContent, final int msgId) {
		SMSBroker smsMessage;		
		// Retrieve account by "account_label"
				Account account = daoService.getAccByLabel(labelAccount);
				// - filling SMS table
				Sms sms = new Sms();
				sms.setAcc(account);
				sms.setApp(app);
				sms.setInitialId(msgId);
				sms.setPhone(smsPhone);
				sms.setStateAsEnum(SmsStatus.CREATED);
				// add sms
				daoService.addSms(sms);

				// - vérifier le num est backlisté ou pas 
				int bla = daoService.getBlackLListByPhone(smsPhone);
				// - si backlist, alors "SMS_STATE" à jour
				if (bla != 0) {
					// state ERROR PRE BLACKLISTE	
					sms.setStateAsEnum(SmsStatus.ERROR_PRE_BL);
					// update sms
					daoService.updateSms(sms); 
				} else {	
					// state IN PROGRESS	
					sms.setStateAsEnum(SmsStatus.IN_PROGRESS);
					// update sms
					daoService.updateSms(sms); 
					account.setConsumedSms(account.getConsumedSms() + 1);
					// update account
					daoService.updateAccount(account); 
					app.setConsumedSms(app.getConsumedSms() + 1);
					// update application
					daoService.updateApplication(app);
					
					// - call send SMS
					smsMessage = new SMSBroker();
					smsMessage.setId(sms.getId());
					smsMessage.setMessage(msgContent);
					smsMessage.setRecipient(smsPhone);
					return smsMessage;
				}	
				
		return null;

	}

	/**
	 * both Account and Application quota must be longer than nbDest.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	private Boolean checkQuotaForThird(final Application app, final Integer nbDest) {
		Account acc = app.getAcc();
		long quotaAcc = acc.getQuota() - acc.getConsumedSms(); 
		
		if (quotaAcc < nbDest) { return false; 
		} else {
			long quotaApp = app.getQuota() - app.getConsumedSms();
			return !(quotaApp < nbDest);
		}
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
	
}
