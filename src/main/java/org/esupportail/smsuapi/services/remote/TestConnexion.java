package org.esupportail.smsuapi.services.remote;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xphp8691
 *
 */
public class TestConnexion {

	@Autowired private ApplicationService applicationService;
	@Autowired private DomainService domainService;
	
	/**
	 * A logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * 
	 */
	public TestConnexion() {
		super();
	}	
	
	/**
	 * @see org.esupportail.smsuapi.services.remote.TestConnexion#testConnexion()
	 */
	public String testConnexion() {
		return domainService.testConnexion();
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(final ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public DomainService getDomainService() {
		return domainService;
	}

	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}
	
	
}
