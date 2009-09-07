/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.Iterator;
import java.util.Set;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.services.remote.AbstractIpProtectedWebService;

import org.esupportail.commons.utils.Assert;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierMessageException;
import org.esupportail.ws.remote.beans.TrackInfosToThird;


/**
 * The basic implementation of the information remote service.
 */
public class SendTrackThirdImpl extends AbstractIpProtectedWebService implements SendTrackThird {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 4480257087458550019L;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * The application service.
	 */
	private ApplicationService applicationService;
	
	/**
	 * The domain service.
	 */
	private DomainService domainService;
	
	/**
	 * Bean constructor.
	 */
	public SendTrackThirdImpl() {
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
	
		
	

	public TrackInfosToThird getTrackInfosToThird(final Integer msgId) 
				throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException {
		logger.debug("WS SendTrackThird receives the client request with parameter msgId = " + msgId);
		TrackInfosToThird infosToThird = new TrackInfosToThird();
		infosToThird.setNbDestTotal(domainService.getNbDest(msgId));
		infosToThird.setNbDestBlackList(domainService.getNbDestBackList(msgId));
		infosToThird.setNbSentSMS(domainService.getNbSentSMS(msgId));
		infosToThird.setNbProgressSMS(domainService.getNbProgressSMS(msgId));
		infosToThird.setNbErrorSMS(domainService.getNbErrorSMS(msgId));
		infosToThird.setListNumErreur(domainService.getListNumErreur(msgId));
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(300);
			sb.append("Response TrackInfosToThird object, for the client of WS SendTrackThird : ");
			sb.append("TrackInfos.NbDestTotal : ").append(infosToThird.getNbDestTotal().toString());
			sb.append("TrackInfos.NbSentSMS : ").append(infosToThird.getNbSentSMS().toString());
			sb.append("TrackInfos.NbProgressSMS : ").append(infosToThird.getNbProgressSMS().toString());
			sb.append("TrackInfos.NbDestBlackList :").append(infosToThird.getNbDestBlackList().toString());
			sb.append("TrackInfos.NbErrorSMS : ").append(infosToThird.getNbErrorSMS().toString());
			logger.debug(sb.toString());
		
		Set<String> listnums = infosToThird.getListNumErreur();
		Iterator<String> iter = listnums.iterator();
	    while (iter.hasNext()) {
	    	String phone = (String) iter.next();
	    	logger.debug("TrackInfosToThird.NumErreur : " + phone);
	    	}
		}
		return infosToThird;
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
