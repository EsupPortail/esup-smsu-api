package org.esupportail.smsuapi.services.remote;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.beans.AbstractApplicationAwareBean;
import org.esupportail.smsuapi.utils.NotNull;
import org.esupportail.smsuapi.utils.AnnotationsChecker;
import org.esupportail.smsuapi.domain.DomainService;

/**
 * @author xphp8691
 *
 */
public class TestConnexion extends AbstractApplicationAwareBean {
	
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1L;
	
	/**
	 * The application service.
	 */
	@NotNull private ApplicationService applicationService;
	
	/**
	 * The domain service.
	 */
	@NotNull private DomainService domainService;
	
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
	 * @see org.esupportail.commons.beans.AbstractApplicationAwareBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		AnnotationsChecker.check(this);
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
