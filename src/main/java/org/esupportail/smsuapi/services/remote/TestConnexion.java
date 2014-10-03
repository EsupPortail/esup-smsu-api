package org.esupportail.smsuapi.services.remote;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.domain.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

public class TestConnexion {

	@Autowired private DomainService domainService;
	
	/**
	 * A logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(this.getClass());
	
	
	/**
	 * @see org.esupportail.smsuapi.services.remote.TestConnexion#testConnexion()
	 */
	public String testConnexion() {
		return domainService.testConnexion();
	}

	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}
	
	
}
