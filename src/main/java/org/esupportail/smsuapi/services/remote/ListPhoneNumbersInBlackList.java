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
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The basic implementation of the information remote service.
 */
public class ListPhoneNumbersInBlackList {

	@Autowired private ApplicationService applicationService;
	@Autowired private DomainService domainService;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(this.getClass());
	
	
	/**
	 * Bean constructor.
	 */
	public ListPhoneNumbersInBlackList() {
		super();
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		logger.info("Receive request for getListPhoneNumbersInBlackList");

		Set<String> listPhoneNumbersInBlackList = domainService.getListPhoneNumbersInBlackList();
		 
		 {
				final StringBuilder sb = new StringBuilder(500);
				sb.append("Response for getListPhoneNumbersInBlackList request :");
				for (String phone : listPhoneNumbersInBlackList) {
				sb.append(" - phone number in blacklist = ").append(phone);	
				}
				logger.info(sb.toString());
			}
		return listPhoneNumbersInBlackList;
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
