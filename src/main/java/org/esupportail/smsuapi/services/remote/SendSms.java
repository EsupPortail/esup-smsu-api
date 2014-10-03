/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.services.remote; 

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.business.SendSmsManager;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the information remote service.
 */
public class SendSms {
	@Autowired protected SendSmsManager sendSmsManager;
	
	/**
	 * A logger.
	 */
	protected final Logger logger = Logger.getLogger(this.getClass());
	
	
	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#mayCreateAccountCheckQuotaOk(java.lang.Integer, java.lang.String)
	 */
	public void mayCreateAccountCheckQuotaOk(final Integer nbDest, final String labelAccount) 
	throws InsufficientQuotaException {
		logger.info("mayCreateAccountCheckQuotaOk method with parameters : " + 
				     " - nbDest = " + nbDest + 
				     " - labelAccount = " + labelAccount);
		sendSmsManager.mayCreateAccountAndCheckQuotaOk(nbDest, labelAccount);
	}
		

	/**
	 * @throws InsufficientQuotaException 
	 * @throws AuthenticationFailed 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#sendSMS(java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.String, 
	 * java.lang.String, 
	 * java.lang.String)
	 */
	public Integer sendSMS(final Integer msgId,
			final Integer senderId, final Integer unused,
			final Integer unused2, final String[] smsPhones,
			final String labelAccount, final String msgContent) throws InsufficientQuotaException {
		
		logger.info("Receive from SendSms client message : " + 
				     " - message id = " + msgId + 
				     " - sender id = " + senderId + 
				     " - recipient phone number = " + smsPhones + 
				     " - user label account = " + labelAccount + 
				     " - message = " + msgContent);
		
		return sendSmsManager.sendSMS(msgId, senderId, smsPhones, labelAccount, msgContent);		
		
	}

}
