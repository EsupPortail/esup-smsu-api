/**
 * ESUP-Portail Example Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-example
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.Set;

import org.esupportail.commons.services.application.ApplicationService;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The basic implementation of the information remote service.
 */
public class SendTrack {
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	@Autowired private ApplicationService applicationService;
	@Autowired private DomainService domainService;
	
	/**
	 * Bean constructor.
	 */
	public SendTrack() {
		super();
	}
	
	public TrackInfos getTrackInfos(final Integer msgId) 
			throws UnknownMessageIdException {
		logger.info("WS SendTrack receives the client request with parameter msgId = " + msgId);
		TrackInfos infos = new TrackInfos();
		infos.setNbDestTotal(domainService.getNbDest(msgId));
		infos.setNbDestBlackList(domainService.getNbDestBlackList(msgId));
		infos.setNbSentSMS(domainService.getNbSentSMS(msgId));
		infos.setNbProgressSMS(domainService.getNbProgressSMS(msgId));
		infos.setNbErrorSMS(domainService.getNbErrorSMS(msgId));
		infos.setListNumErreur(domainService.getListNumErreur(msgId));
		
		logger.info("Response TrackInfos object, for the client of WS SendTrack : " + 
				     "TrackInfos.NbDestTotal : " + infos.getNbDestTotal().toString() + 
				     "TrackInfos.NbSentSMS : " + infos.getNbSentSMS().toString() + 
				     "TrackInfos.NbProgressSMS : " + infos.getNbProgressSMS().toString() + 
				     "TrackInfos.NbDestBlackList :" + infos.getNbDestBlackList().toString() + 
				     "TrackInfos.NbErrorSMS : " + infos.getNbErrorSMS().toString());
		
		Set<String> listnums = infos.getListNumErreur();
		for (String phone : listnums) {
			logger.info("TrackInfos.NumErreur : " + phone);
	    	}
	    
		return infos;
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
