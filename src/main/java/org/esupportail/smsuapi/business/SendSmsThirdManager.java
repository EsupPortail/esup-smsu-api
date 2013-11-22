package org.esupportail.smsuapi.business;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;

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
				
		Application app = clientManager.getApplicationOrNull();
		Account account = app == null ? null : app.getAcc();
		checkQuotaOk(smsPhoneList.size(), account); 

		List<SMSBroker> smsMessageList = new ArrayList();

		for (String phone : smsPhoneList) {
			SMSBroker smsMessage = saveSMSNoCheck(msgId, null, phone, account, msgContent, app);
			// creer toute la liste
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
		schedulerUtils.launchSuperviseSmsSending(smsMessageList);
	}

}
