package org.esupportail.smsuapi.business;

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
	protected final Logger logger = new LoggerImpl(getClass());

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
	throws UnknownIdentifierApplicationException, InsufficientQuotaException {

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
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {

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

			SMSBroker smsMessage = saveSMS(msgId, perId, bgrId, svcId, smsPhone, labelAccount, msgContent);

			if (smsMessage != null) { 
				// launch the task witch manage the sms sending
				schedulerUtils.launchSuperviseSmsSending(smsMessage);
			}
	}


	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	private SMSBroker saveSMS(final Integer msgId, final Integer perId, final Integer bgrId, 
			final Integer svcId, final String smsPhone, 
			final String labelAccount, final String msgContent) {

		Application app = clientManager.getApplicationOrNull();
		
		// check if the sms already exists (in case of FO problem...)
		if (msgId != null && app != null) {
			if (!daoService.getSms(app, msgId, smsPhone).isEmpty()) {
				logger.error("SMS already sent! Check for a problem with the application : " + app.getName());
				return null;
			}
		}

		try {
			Account account = mayCreateAccountAndCheckQuotaOk(1, labelAccount);
			return saveSMSNoCheck(msgId, perId, bgrId, svcId, smsPhone, account, msgContent, app);
		} catch (UnknownIdentifierApplicationException e) {
			logger.error(e);
			return null;
		} catch (InsufficientQuotaException e) {
			if (logger.isDebugEnabled()) {
				logger.warn("Error Quota, SMS de validation du compte not sent", e);
			}
			return null;
		}
	}

	protected SMSBroker saveSMSNoCheck(final Integer msgId, final Integer perId,
			final Integer bgrId, final Integer svcId, final String smsPhone,
			Account account, final String msgContent,
			Application app) {	
		
		boolean isBlacklisted = daoService.getBlackLListByPhone(smsPhone) != 0;
		
		Sms sms = new Sms();
		sms.setAcc(account);
		sms.setApp(app);
		sms.setGrpSenderId(bgrId);
		sms.setInitialId(msgId);
		sms.setPhone(smsPhone);
		sms.setSenderId(perId);
		sms.setSvcId(svcId);
		sms.setStateAsEnum(isBlacklisted ? SmsStatus.ERROR_PRE_BL : SmsStatus.IN_PROGRESS);
		daoService.addSms(sms);

		if (isBlacklisted) {
			return null;
		} else {
			account.setConsumedSms(account.getConsumedSms() + 1);
			daoService.updateAccount(account); 
		
			app.setConsumedSms(app.getConsumedSms() + 1);
			daoService.updateApplication(app);

			return new SMSBroker(sms.getId(), smsPhone, msgContent);
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
