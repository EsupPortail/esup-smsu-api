/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.remote.AbstractIpProtectedWebService;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;

/**
 * The basic implementation of the information remote service.
 */
public class ReportingImpl extends AbstractIpProtectedWebService implements Reporting {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 4480257087458550019L;

	/**
	 * The application service.
	 */
	private ApplicationService applicationService;
	
	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * Bean constructor.
	 */
	public ReportingImpl() {
		super();
	}

	/**
	 * @see org.esupportail.commons.services.remote.AbstractIpProtectedWebService#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Assert.notNull(applicationService, 
				"property applicationService of class " + this.getClass().getName() 
				+ " can not be null");
		Assert.notNull(domainService, 
				"property domainService of class " + this.getClass().getName() 
				+ " can not be null");
	}
	

	public ReportingInfos getStats(final int month, final int year) 
				throws UnknownMonthIndexException, UnknownIdentifierApplicationException {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(300);
			sb.append("getStats call method parameters : ");
			sb.append(" - month : ").append(month);	
			sb.append(" - year : ").append(year);
			logger.debug(sb.toString());
		}	
		ReportingInfos reportingInfos = domainService.getStats(month, year);
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(300);
			if (reportingInfos != null) {
			sb.append("reportingInfos is : ");
			sb.append(" - reportingInfos account is : ").append(reportingInfos.getAccountLabel());	
			sb.append(" - reportingInfos nbSms is : ").append(reportingInfos.getNbSms());
			sb.append(" - reportingInfos nbSmsInError is : ").append(reportingInfos.getNbSmsInError());
			sb.append(" - reportingInfos month is : ").append(reportingInfos.getMonth());
			sb.append(" - reportingInfos year is : ").append(reportingInfos.getYear());
			} else {
				sb.append("reportingInfos is null ");
			}
			logger.debug(sb.toString());
		}	
	   return reportingInfos;	
	}

	/**
	 * @param applicationService the applicationService to set
	 */
	public void setApplicationService(final ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	/**
	 * @param domainService the domainService to set
	 */
	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

}
