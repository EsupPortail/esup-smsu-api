package org.esupportail.smsuapi.services.remote;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.remote.AbstractIpProtectedWebService;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsuapi.domain.DomainService;

/**
 * @author xphp8691
 *
 */
public class TestConnexionImpl extends AbstractIpProtectedWebService implements TestConnexion {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1L;
	
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
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * 
	 */
	public TestConnexionImpl() {
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
	
	
	/**
	 * @see org.esupportail.smsuapi.services.remote.TestConnexion#testConnexion()
	 */
	public String testConnexion() {
		// TODO Auto-generated method stub
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
