package org.esupportail.smsuapi.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Blacklist;


/**
 * Business layer concerning smsu service.
 *
 */
public class BlackListManager {
	
	/**
	 * Log4j logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	
	//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public BlackListManager() {
		super();
	}
	
	//////////////////////////////////////////////////////////////
	// WS Blacklist methods
	//////////////////////////////////////////////////////////////
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
		return daoService.isPhoneNumberInBlackList(phoneNumber);
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		 List<Blacklist> listPhoneNumbersInBlackList = daoService.getListPhoneNumbersInBlackList();
		 Set<String> setPhoneNumbersInBlackList = new HashSet<String>();
		 for (Blacklist bl : listPhoneNumbersInBlackList) {
				setPhoneNumbersInBlackList.add(bl.getPhone());
			}
		return setPhoneNumbersInBlackList;
	}

	
	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////
	
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	
}
