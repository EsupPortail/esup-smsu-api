package org.esupportail.smsuapi.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
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
	private final Logger logger = Logger.getLogger(getClass());

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
		logger.info("Receive request for isPhoneNumberInBlackList : " + phoneNumber);
		boolean retVal = daoService.isPhoneNumberInBlackList(phoneNumber);
		logger.info("Response for getListPhoneNumbersInBlackList request : " 
				 + phoneNumber + " is : " + retVal);		
		return retVal;
	}
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		 List<Blacklist> listPhoneNumbersInBlackList = daoService.getListPhoneNumbersInBlackList();
		 Set<String> setPhoneNumbersInBlackList = new HashSet<>();
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
