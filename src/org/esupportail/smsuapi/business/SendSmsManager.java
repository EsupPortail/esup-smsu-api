package org.esupportail.smsuapi.business;

import java.util.LinkedList;
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
public class SendSmsManager {

	/**
	 * Log4j logger.
	 */
	@SuppressWarnings("unused")
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
	public SendSmsManager() {
		super();
	}

	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public Boolean isQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {

		String clientName = clientManager.getClientName();
		Application app = clientManager.getApplicationByCertificateCN(clientName);
		//Application app = daoService.getApplicationByName(clientName);
		long quotaAcc = 0;
		long quotaApp = 0;
		Boolean retVal = false;

		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else {
			Account acc = daoService.getAccByLabel(labelAccount);
			if (acc == null) { 
				// - cr�er nouveau account
				Account newacc = new Account();
				newacc.addToApplications(app);
				newacc.setLabel(labelAccount);
				newacc.setQuota((long) 0);
				newacc.setConsumedSms((long) 0);
				daoService.addAccount(newacc);
				throw new InsufficientQuotaException("Quota error");
			} else {
				quotaAcc = acc.getQuota() - acc.getConsumedSms();
				if (quotaAcc > nbDest) { 
					quotaApp = app.getQuota() - app.getConsumedSms();
					if (quotaApp > nbDest) {
						retVal = true;
					} else {
						throw new InsufficientQuotaException("Quota error");
					}
				} else {
					throw new InsufficientQuotaException("Quota error");
				}
			}
		}
		return retVal;
	}

	/**
	 * @param msgId 
	 * @param perId 
	 * @param bgrId 
	 * @param svcId 
	 * @param smsPhone 
	 * @param labelAccount 
	 * @param msgContent 
	 * @throws UnknownIdentifierApplicationException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	public void sendSMS(final Integer msgId, final Integer perId, final Integer bgrId, 
			final Integer svcId, final String smsPhone, 
			final String labelAccount, final String msgContent) {

		String clientName = clientManager.getClientName();
		Application app = clientManager.getApplicationByCertificateCN(clientName);

		if (app == null) { 
			logger.error("An unknown application tries to send a SMS : [" + clientName + "]");
		} else {
			SMSBroker smsMessage = saveSMS(msgId, perId, bgrId, svcId, smsPhone, labelAccount, msgContent);
			// TO DE-COMMENT
			if (smsMessage != null) { 
				// launch the task witch manage the sms sending
				schedulerUtils.launchSuperviseSmsSending(smsMessage);
				//smsSender.sendMessage(smsMessage); 
			}
		}
	}


	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	private SMSBroker saveSMS(final Integer msgId, final Integer perId, final Integer bgrId, 
			final Integer svcId, final String smsPhone, 
			final String labelAccount, final String msgContent) {

		long quotaAcc = 0;
		long quotaApp = 0;
		Boolean retVal = false;

		String clientName = clientManager.getClientName();
		Application app = clientManager.getApplicationByCertificateCN(clientName);
		// chech if the sms already exists (in case of FO problem...)
		List<Sms> lstSms = new LinkedList<Sms>();
		if (msgId != null) {
			daoService.getSms(app, msgId, smsPhone);
		}
		if (!lstSms.isEmpty()) {
			logger.error("SMS already sent! Check for a problem with the application : " + app.getName());
		} else {
			// Application app = daoService.getApplicationByName(clientName);
			SMSBroker smsMessage;

			// Pour la validation de l'adh�sion
			Account acc = daoService.getAccByLabel(labelAccount);
			if (acc == null) { 
				// - cr�er nouveau account
				Account newacc = new Account();
				newacc.addToApplications(app);
				newacc.setLabel(labelAccount);
				newacc.setQuota((long) 0);
				newacc.setConsumedSms((long) 0);
				daoService.addAccount(newacc);
			} else {
				quotaAcc = acc.getQuota() - acc.getConsumedSms();
				if (quotaAcc > 0) { 
					quotaApp = app.getQuota() - app.getConsumedSms();
					if (quotaApp > 0) {
						retVal = true;
					} else {
						retVal = false;
					}
				} else {
					retVal = false;
				}
			}
			if (retVal)	{
				if (app != null) { 
					// retrieve account by "account_label"
					Account account = daoService.getAccByLabel(labelAccount);
					// - remplir la table SMS
					Sms sms = new Sms();
					sms.setAcc(account);
					sms.setApp(app);
					sms.setGrpSenderId(bgrId);
					sms.setInitialId(msgId);
					sms.setPhone(smsPhone);
					sms.setSenderId(perId);
					sms.setSvcId(svcId);
					sms.setStateAsEnum(SmsStatus.CREATED);
					// add sms
					daoService.addSms(sms);

					// - v�rifier le num est backlist� ou pas 
					int bla = daoService.getBlackLListByPhone(smsPhone);
					// - si backlist, alors "SMS_STATE" � jour
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

				}
			} else { 
				if (logger.isDebugEnabled()) {
					logger.warn("Error Quota, SMS de validation du compte non envoy�");
				}
			}
		}
		return null;

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
