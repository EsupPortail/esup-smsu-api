/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the information remote service.
 */
public class SendSms {
	@Autowired protected ApplicationService applicationService;
	@Autowired protected DomainService domainService;
	
	/**
	 * A logger.
	 */
	protected final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * Bean constructor.
	 */
	public SendSms() {
		super();
	}

	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#mayCreateAccountCheckQuotaOk(java.lang.Integer, java.lang.String)
	 */
	public void mayCreateAccountCheckQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {
		logger.info("mayCreateAccountCheckQuotaOk method with parameters : " + 
				     " - nbDest = " + nbDest + 
				     " - labelAccount = " + labelAccount);
		domainService.mayCreateAccountCheckQuotaOk(nbDest, labelAccount);
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
			final Integer senderId, final Integer unused,
			final Integer unused2, final String smsPhone,
			final String labelAccount, final String msgContent) {
		
		logger.info("Receive from SendSms client message : " + 
				     " - message id = " + msgId + 
				     " - sender id = " + senderId + 
				     " - recipient phone number = " + smsPhone + 
				     " - user label account = " + labelAccount + 
				     " - message = " + msgContent);
		
		domainService.sendSMS(msgId, senderId, smsPhone, labelAccount, msgContent);
		
		
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
