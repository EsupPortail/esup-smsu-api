/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.io.Serializable;
import java.util.List;

import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;


/**
 * The interface of the information remote service.
 */
public interface SendSmsThird extends Serializable {

	/**
	 * send SMS by Third applications.
	 */
	void sendSMSByThird(List<String> smsPhoneList, String msgContent, int msgId)
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException;
	

}
