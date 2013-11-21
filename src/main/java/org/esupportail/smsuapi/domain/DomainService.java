/**
 * ESUP-Portail esup-smsu-api - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu-api
 */
package org.esupportail.smsuapi.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.application.Version;

import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.User;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;


/**
 * The domain service interface.
 */
public interface DomainService extends Serializable {

	//////////////////////////////////////////////////////////////
	// User
	//////////////////////////////////////////////////////////////

	/**
	 * @param id
	 * @return the User instance that corresponds to an id.
	 * @throws UserNotFoundException
	 */
	User getUser(String id) throws UserNotFoundException;

	/**
	 * @return the list of all the users.
	 */
	List<User> getUsers();

	/**
	 * Update a user.
	 * @param user
	 */
	void updateUser(User user);

	/**
	 * Update a user's information (retrieved from the LDAP directory for instance).
	 * @param user
	 */
	void updateUserInfo(User user);
	
	/**
	 * Add an administrator.
	 * @param user
	 */
	void addAdmin(User user);

	/**
	 * Delete an administrator.
	 * @param user
	 */
	void deleteAdmin(User user);
	
	//////////////////////////////////////////////////////////////
	// Authorizations
	//////////////////////////////////////////////////////////////

	/**
	 * @param currentUser
	 * @return 'true' if the user can view administrators.
	 */
	boolean userCanViewAdmins(User currentUser);
	
	/**
	 * @param user 
	 * @return 'true' if the user can grant the privileges of administrator.
	 */
	boolean userCanAddAdmin(User user);

	/**
	 * @param user 
	 * @param admin
	 * @return 'true' if the user can revoke the privileges of an administrator.
	 */
	boolean userCanDeleteAdmin(User user, User admin);

	//////////////////////////////////////////////////////////////
	// WS SendTrack methods
	//////////////////////////////////////////////////////////////
	/**
	 * @param msgId 
	 * @return the number of SMS recipients.
	 */
	int getNbDest(Integer msgId) throws UnknownIdentifierMessageException, UnknownIdentifierApplicationException;
	
	/**
	 * @param msgId 
	 * @return the non-authorized phone numbers (in black list).
	 */
	int getNbDestBlackList(Integer msgId) throws UnknownIdentifierApplicationException;
	
	/**
	 * @param msgId 
	 * @return the number of sent SMS.
	 */
	int getNbSentSMS(Integer msgId) throws UnknownIdentifierApplicationException;

	/**
	 * @return the number of SMS in progress.
	 */
	int getNbProgressSMS(Integer msgId) throws UnknownIdentifierApplicationException;
	
	/**
	 * @return the number of SMS in error.
	 */
	int getNbErrorSMS(Integer msgId) throws UnknownIdentifierApplicationException;
	
	/**
	 * @return the number of SMS in error.
	 */
	Set<String> getListNumErreur(Integer msgId);

	/**
	 * @return the sms object corresponding to msgId & phoneNumber
	 * @throws UnknownIdentifierApplicationException 
	 */
	Sms getSms(Integer msgId, String phoneNumber) throws UnknownIdentifierApplicationException;

	//////////////////////////////////////////////////////////////
	// WS SendSms methods
	//////////////////////////////////////////////////////////////
	/**
	 * @param nbDest 
	 * @param labelAccount 
	 */
	void mayCreateAccountCheckQuotaOk(Integer nbDest, String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException;
	
	/**
	 * send SMS.
	 * @param msgId 
	 * @param perId 
	 * @param smsPhone 
	 * @param labelAccount 
	 * @param msgContent 
	 * @throws UnknownIdentifierApplicationException 
	 */
	void sendSMS(Integer msgId, Integer perId, 
			String smsPhone, String labelAccount, 
			String msgContent);

	//////////////////////////////////////////////////////////////
	// WS SendSmsByThird methods
	//////////////////////////////////////////////////////////////
	void sendSMSByThird(List<String> smsPhoneList,
			 String msgContent, int msgId) throws UnknownIdentifierApplicationException, 
				InsufficientQuotaException; 
	
	
	/////////////////////////////////////////////////////////////
	// WS BlackList methods
	//////////////////////////////////////////////////////////////
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	boolean isPhoneNumberInBlackList(String phoneNumber);
	
	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	Set<String>  getListPhoneNumbersInBlackList();

	/////////////////////////////////////////////////////////////
	// WS Reporting method
	//////////////////////////////////////////////////////////////
	/**
	 * get Stats interface.
	 * @param month
	 * @param year
	 */
	ReportingInfos getStats(int month, int year) 
					throws UnknownMonthIndexException, UnknownIdentifierApplicationException;
	
	/**
	 * @param name
	 * @return the application dao
	 */
	Application getApplicationByName(String name);

	/**
	 * @return a function to test the connexion.
	 */
	String testConnexion();
	
}
