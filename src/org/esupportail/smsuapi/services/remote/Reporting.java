/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.io.Serializable;

import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;




/**
 * The interface of the information remote service.
 */
public interface Reporting extends Serializable {

	/**
	 * get Stats interface.
	 * @param month
	 * @param year
	 */
	ReportingInfos getStats(int month, int year) 
					throws UnknownMonthIndexException, UnknownIdentifierApplicationException;
	

}
