/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.List;

import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;

/**
 * The basic implementation of the information remote service.
 */
public class SendSmsThirdImpl extends SendSmsImpl implements SendSmsThird {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 4480257087458550019L;

	
	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#sendSMS(java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.String, 
	 * java.lang.String, 
	 * java.lang.String)
	 */
	public void sendSMSByThird(final List<String> smsPhoneList,
			final String msgContent, final int msgId) throws UnknownIdentifierApplicationException, 
			InsufficientQuotaException {
		
		{
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Receive from SendSmsThird client message : ");
			for (String phone : smsPhoneList) {
			    sb.append(" - recipient phone number = ").append(phone);	
			}
			sb.append(" - message = ").append(msgContent);
			logger.info(sb.toString());
		}
		
		domainService.sendSMSByThird(smsPhoneList, msgContent, msgId);
		
		
	}
}
