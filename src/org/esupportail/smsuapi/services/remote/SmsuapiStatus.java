/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.io.Serializable;
import java.util.List;

import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.ws.remote.beans.MsgIdAndPhone;

/**
 * The interface of the information remote service.
 */
public interface SmsuapiStatus extends Serializable {

	/**
	 * Get the status for a list of msgIds
	 * @param msgIds
	 * @return the list of status for each msgIds
	 * @throws UnknownIdentifierApplicationException 
	 */
	List<String> getStatus(List<MsgIdAndPhone> listMsgIdAndPhone) throws UnknownIdentifierApplicationException;

}
