/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.List;

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
public class SendSmsThirdImpl extends AbstractIpProtectedWebService implements SendSmsThird {

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
	public SendSmsThirdImpl() {
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
	 * @see org.esupportail.smsuapi.services.remote.SendSms#sendSMS(java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.Integer, 
	 * java.lang.String, 
	 * java.lang.String, 
	 * java.lang.String)
	 */
	public void sendSMSByThird(final List<String> smsPhoneList,
			final String msgContent, final int msgId) throws UnknownIdentifierApplicationException, 
			InsufficientQuotaException {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Receive from SendSmsThird client message : ");
			for (String phone : smsPhoneList) {
			    sb.append(" - recipient phone number = ").append(phone);	
			}
			sb.append(" - message = ").append(msgContent);
			logger.debug(sb.toString());
		}
		
		domainService.sendSMSByThird(smsPhoneList, msgContent, msgId);
		
		
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
