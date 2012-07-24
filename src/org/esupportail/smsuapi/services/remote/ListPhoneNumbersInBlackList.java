/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.io.Serializable;
import java.util.Set;




/**
 * The interface of the information remote service.
 */
public interface ListPhoneNumbersInBlackList extends Serializable {

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	Set<String> getListPhoneNumbersInBlackList();
	

}