/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.services.remote; 

import java.util.LinkedList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.ws.remote.beans.MsgIdAndPhone;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The basic implementation of the information remote service.
 */
public class SmsuapiStatus {

	@Autowired private DomainService domainService;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * Bean constructor.
	 */
	public SmsuapiStatus() {
		super();
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 * @throws AuthenticationFailed 
	 */
	public List<String> getStatus(List<MsgIdAndPhone> listMsgIdAndPhone) {
		{
			final StringBuilder sb = new StringBuilder(500);
			logger.info("Receive request for SmsuapiStatus.getStatus:");
			for (MsgIdAndPhone m : listMsgIdAndPhone) sb.append(" " + m);
			logger.info(sb.toString());
		}
		 
		List<String> l = new LinkedList<String>();

		for (MsgIdAndPhone m : listMsgIdAndPhone) {
			Sms sms = domainService.getSms(m.getMsgId(), m.getPhoneNumber());
			l.add(sms == null ? null : sms.getStateAsEnum().toString());
		}
		return l;
	}
	
	/**
	 * @param domainService the domainService to set
	 */
	public void setDomainService(final DomainService domainService) {
		this.domainService = domainService;
	}

}
