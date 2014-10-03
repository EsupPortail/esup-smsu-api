/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.Set;

import org.apache.log4j.Logger;
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
	private final Logger logger = Logger.getLogger(getClass());

	@Autowired private DomainService domainService;
	
	
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
	 * @param domainService the domainService to set
	 */
	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

}
