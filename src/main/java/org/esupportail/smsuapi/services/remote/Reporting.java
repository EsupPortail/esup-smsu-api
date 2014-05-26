/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.services.remote; 

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.business.ReportingManager;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the information remote service.
 */
public class Reporting {

	@Autowired private ReportingManager reportingManager;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());

	
	public ReportingInfos getStats(final int month, final int year) 
				throws UnknownMonthIndexException {
		logger.info("getStats call method parameters : " + 
				     " - month : " + month + 
				     " - year : " + year);
		ReportingInfos reportingInfos = reportingManager.getStats(month, year);
		
		{
			final String s;
			if (reportingInfos != null) {
				s = "reportingInfos is : " + 
					" - reportingInfos account is : " + reportingInfos.getAccountLabel() + 
					" - reportingInfos nbSms is : " + reportingInfos.getNbSms() + 
					" - reportingInfos nbSmsInError is : " + reportingInfos.getNbSmsInError() + 
					" - reportingInfos month is : " + reportingInfos.getMonth() + 
					" - reportingInfos year is : " + reportingInfos.getYear();
			} else {
				s = "reportingInfos is null ";
			}
			logger.info(s);
		}	
	   return reportingInfos;	
	}

}
