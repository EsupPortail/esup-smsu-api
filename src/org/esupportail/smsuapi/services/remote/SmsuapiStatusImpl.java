/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.LinkedList;
import java.util.List;
import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.remote.AbstractIpProtectedWebService;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.ws.remote.beans.MsgIdAndPhone;


/**
 * The basic implementation of the information remote service.
 */
public class SmsuapiStatusImpl extends AbstractIpProtectedWebService implements SmsuapiStatus {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6357313623247229092L;

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
	public SmsuapiStatusImpl() {
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
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 * @throws UnknownIdentifierApplicationException 
	 */
	public List<String> getStatus(List<MsgIdAndPhone> listMsgIdAndPhone) throws UnknownIdentifierApplicationException {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(500);
			logger.debug("Receive request for SmsuapiStatusImpl.getStatus:");
			for (MsgIdAndPhone m : listMsgIdAndPhone) sb.append(" " + m);
			logger.debug(sb.toString());
		}
		 
		List<String> l = new LinkedList<String>();

		for (MsgIdAndPhone m : listMsgIdAndPhone) {
			Sms sms = domainService.getSms(m.getMsgId(), m.getPhoneNumber());
			l.add(sms == null ? null : sms.getStateAsEnum().toString());
		}
		return l;
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
