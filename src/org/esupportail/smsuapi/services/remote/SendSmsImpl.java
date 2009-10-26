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
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;

/**
 * The basic implementation of the information remote service.
 */
public class SendSmsImpl extends AbstractIpProtectedWebService implements SendSms {

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
	public SendSmsImpl() {
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
	 * @see org.esupportail.smsuapi.services.remote.SendSms#isQuotaOk(java.lang.Integer, java.lang.String)
	 */
	public Boolean isQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("isQuotaOK method with parameters : ");
			sb.append(" - nbDest = ").append(nbDest);
			sb.append(" - labelAccount = ").append(labelAccount);
			logger.debug(sb.toString());
		}
		return domainService.isQuotaOk(nbDest, labelAccount);
	}


	/**
	 * @throws UnknownIdentifierApplicationException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#sendSMS(java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.String, 
	 * java.lang.String, 
	 * java.lang.String)
	 */
	public void sendSMS(final Integer msgId,
			final Integer perId, final Integer bgrId,
			final Integer svcId, final String smsPhone,
			final String labelAccount, final String msgContent) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Receive from SendSms client message : ");
			sb.append(" - message id = ").append(msgId);
			sb.append(" - sender id = ").append(perId);
			sb.append(" - group sender id = ").append(bgrId);
			sb.append(" - service id = ").append(svcId);
			sb.append(" - recipient phone number = ").append(smsPhone);
			sb.append(" - user label account = ").append(labelAccount);
			sb.append(" - message = ").append(msgContent);
			logger.debug(sb.toString());
		}
		
		domainService.sendSMS(msgId, perId, bgrId, svcId, smsPhone, labelAccount, msgContent);
		
		
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
