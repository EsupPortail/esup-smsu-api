/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.business.ReportingManager;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the information remote service.
 */
public class Reporting {

	@Autowired private ApplicationService applicationService;
	@Autowired private ReportingManager reportingManager;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * Bean constructor.
	 */
	public Reporting() {
		super();
	}

	public ReportingInfos getStats(final int month, final int year) 
				throws UnknownMonthIndexException, UnknownIdentifierApplicationException {
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

	/**
	 * @param applicationService the applicationService to set
	 */
	public void setApplicationService(final ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

}
