package org.esupportail.smsuapi.business;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.services.scheduler.SchedulerUtils;
import org.esupportail.smsuapi.services.sms.ISMSSender;

/**
 * Business layer concerning smsu service.
 *
 */
public class SendSmsThirdManager extends SendSmsManager {
	
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
		return saveSMSNoCheck(msgId, null, null, null, smsPhone, labelAccount, msgContent, app);
	}

	/**
	 * both Account and Application quota must be longer than nbDest.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	private Boolean checkQuotaForThird(final Application app, final Integer nbDest) {
		return app.getAcc().checkQuota(nbDest) && app.checkQuota(nbDest);
	}
	
}
