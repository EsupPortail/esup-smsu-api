/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.io.Serializable;

import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;


/**
 * The interface of the information remote service.
 */
public interface SendSms extends Serializable {

	/**
	 * check Quota. 
	 * @param nbDest 
	 * @param labelAccount 
	 * @throws UnknownIdentifierApplicationException 
	 * @throws InsufficientQuotaException 
	 */
	void mayCreateAccountCheckQuotaOk(Integer nbDest, String labelAccount)
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException;
	
	/**
	 * send SMS.
	 * @param msgId 
	 * @param perId 
	 * @param unused
	 * @param unused2
	 * @param smsPhone 
	 * @param labelAccount 
	 * @param msgContent 
	 */
	void sendSMS(Integer msgId, Integer perId, Integer unused, Integer unused2, 
			String smsPhone, String labelAccount, String msgContent);
	

}
