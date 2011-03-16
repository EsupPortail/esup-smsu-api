/**
 * ESUP-Portail esup-smsu-api-admin - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu-api-admin
 */
package org.esupportail.smsuapi.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.exceptions.ConfigException;
import org.esupportail.commons.exceptions.UserNotFoundException;
import org.esupportail.commons.services.application.Version;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.web.beans.Paginator;
import org.esupportail.smsuapi.business.BlackListManager;
import org.esupportail.smsuapi.business.ClientManager;
import org.esupportail.smsuapi.business.ReportingManager;
import org.esupportail.smsuapi.business.SendSmsManager;
import org.esupportail.smsuapi.business.SendSmsThirdManager;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.User;
import org.esupportail.smsuapi.domain.beans.VersionManager;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierMessageException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;
import org.springframework.beans.factory.InitializingBean;



/**
 * The basic implementation of DomainService.
 * 
 * See /properties/domain/domain-example.xml
 */
public class DomainServiceWithoutLDAPImpl implements DomainService, InitializingBean {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -8200845058340254019L;

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

	/**
	 * {@link SendSmsManager}.
	 */
	private SendSmsManager sendSmsManager;

	/**
	 * {@link SendSmsThirdManager}.
	 */
	private SendSmsThirdManager sendSmsThirdManager;

	/**
	 * {@link BlackListManager}.
	 */
	private BlackListManager blackListManager;

	/**
	 * {@link ReportingManager}.
	 */
	private ReportingManager reportingManager;

	/**
	 *  {@link ClientManager}.
	 */
	private ClientManager clientManager;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * Bean constructor.
	 */
	public DomainServiceWithoutLDAPImpl() {
		super();
	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.daoService, 
				"property daoService of class " + this.getClass().getName() + " can not be null");
	}

	//////////////////////////////////////////////////////////////
	// User
	//////////////////////////////////////////////////////////////

	/**
	 * Set the information of a user from a ldapUser.
	 * @param user 
	 * @param ldapUser 
	 * @return true if the user was updated.
	 */
	private boolean setUserInfo(
			final User user) {
		String displayName = user.getId();
		if (displayName.equals(user.getDisplayName())) {
			return false;
		}
		user.setDisplayName(displayName);
		return true;
	}


	/**
	 * @see org.esupportail.smsuapi.domain.DomainService#updateUserInfo(org.esupportail.smsuapi.domain.beans.User)
	 */
	public void updateUserInfo(final User user) {
		if (setUserInfo(user)) {
			updateUser(user);
		}
	}

	/**
	 * If the user is not found in the database, try to create it from a LDAP search.
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#getUser(java.lang.String)
	 */
	public User getUser(final String id) throws UserNotFoundException {
		User user = daoService.getUser(id);
		if (user == null) {
			user = new User();
			user.setId(id);
			setUserInfo(user);
			daoService.addUser(user);
			logger.info("user '" + user.getId() + "' has been added to the database");
		}
		return user;
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#getUsers()
	 */
	public List<User> getUsers() {
		return this.daoService.getUsers();
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#updateUser(
	 * org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public void updateUser(final User user) {
		this.daoService.updateUser(user);
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#addAdmin(
	 * org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public void addAdmin(
			final User user) {
		user.setAdmin(true);
		updateUser(user);
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#deleteAdmin(
	 * org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public void deleteAdmin(
			final User user) {
		user.setAdmin(false);
		updateUser(user);
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#getAdminPaginator()
	 */
	public Paginator<User> getAdminPaginator() {
		return this.daoService.getAdminPaginator();
	}


	//////////////////////////////////////////////////////////////
	// VersionManager
	//////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#getDatabaseVersion()
	 */
	public Version getDatabaseVersion() throws ConfigException {
		VersionManager versionManager = daoService.getVersionManager();
		if (versionManager == null) {
			return null;
		}
		return new Version(versionManager.getVersion());
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#setDatabaseVersion(java.lang.String)
	 */
	public void setDatabaseVersion(final String version) {
		if (logger.isDebugEnabled()) {
			logger.debug("setting database version to '" + version + "'...");
		}
		VersionManager versionManager = daoService.getVersionManager();
		versionManager.setVersion(version);
		daoService.updateVersionManager(versionManager);
		if (logger.isDebugEnabled()) {
			logger.debug("database version set to '" + version + "'.");
		}
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#setDatabaseVersion(
	 * org.esupportail.commons.services.application.Version)
	 */
	public void setDatabaseVersion(final Version version) {
		setDatabaseVersion(version.toString());
	}

	//////////////////////////////////////////////////////////////
	// Authorizations
	//////////////////////////////////////////////////////////////

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#userCanViewAdmins(
	 * org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public boolean userCanViewAdmins(final User user) {
		if (user == null) {
			return false;
		}
		return user.getAdmin();
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#userCanAddAdmin(
	 * org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public boolean userCanAddAdmin(final User user) {
		if (user == null) {
			return false;
		}
		return user.getAdmin();
	}

	/**
	 * @see org.esupportail.smsuapiadmin.domain.DomainService#userCanDeleteAdmin(
	 * org.esupportail.smsuapiadmin.domain.beans.User, org.esupportail.smsuapiadmin.domain.beans.User)
	 */
	public boolean userCanDeleteAdmin(final User user, final User admin) {
		if (user == null) {
			return false;
		}
		if (!user.getAdmin()) {
			return false;
		}
		return !user.equals(admin);
	}

	//////////////////////////////////////////////////////////////
	// Misc
	//////////////////////////////////////////////////////////////

	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	//////////////////////////////////////////////////////////////
	// WS SendTrack methods
	//////////////////////////////////////////////////////////////
	/**
	 * @return the number of SMS recipients.
	 * @throws UnknownIdentifierMessageException 
	 * @throws UnknownIdentifierApplicationException 
	 */
	public int getNbDest(final Integer msgId) 
	throws UnknownIdentifierMessageException, UnknownIdentifierApplicationException {
		Application app = clientManager.getApplication();
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application : " + clientManager.getClientName());
		} else {
			if (daoService.getNbDest(msgId, app) == 0) {
				throw new UnknownIdentifierMessageException("Unknown message");
			} else {
				return daoService.getNbDest(msgId, app);
			}
		}	
	}

	/**
	 * @return the non-authorized phone numbers (in back list).
	 * @throws UnknownIdentifierApplicationException 
	 */
	@SuppressWarnings("unchecked")
	public int getNbDestBackList(final Integer msgId) throws UnknownIdentifierApplicationException {
		List<String> list = new ArrayList();
		list.add(SmsStatus.ERROR_PRE_BL.name());
		list.add(SmsStatus.ERROR_POST_BL.name());

		Application app = clientManager.getApplication();
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else { return daoService.getNbDestBackList(msgId, app, list); }


	}

	/**
	 * @return the number of sent SMS.
	 * @throws UnknownIdentifierApplicationException 
	 */
	public int getNbSentSMS(final Integer msgId) throws UnknownIdentifierApplicationException {
		Application app = clientManager.getApplication();
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else { return daoService.getNbSentSMS(msgId, app); }


	}

	/**
	 * @return the number of SMS in progress.
	 * @throws UnknownIdentifierApplicationException 
	 */
	public int getNbProgressSMS(final Integer msgId) throws UnknownIdentifierApplicationException {
		Application app = clientManager.getApplication();
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else { return daoService.getNbProgressSMS(msgId, app); }
	}

	/**
	 * @return the number of SMS in error.
	 * @throws UnknownIdentifierApplicationException 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int getNbErrorSMS(final Integer msgId) throws UnknownIdentifierApplicationException {
		List<String> list = new ArrayList();
		list.add(SmsStatus.ERROR.name());
		list.add(SmsStatus.ERROR_PRE_BL.name());
		list.add(SmsStatus.ERROR_POST_BL.name());

		Application app = clientManager.getApplication();
		if (app == null) { 
			throw new UnknownIdentifierApplicationException("Unknown application");
		} else { return daoService.getNbErrorSMS(msgId, app, list); }

	}

	/**
	 * @return the number of SMS in error.
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getListNumErreur(final Integer msgId) {
		// list criteria for HQL query 
		List<String> list = new ArrayList();
		list.add(SmsStatus.ERROR.name());
		list.add(SmsStatus.ERROR_POST_BL.name());
		list.add(SmsStatus.ERROR_PRE_BL.name());

		List<Sms> smslist = new ArrayList<Sms>();

		Application app = clientManager.getApplication();

		if (app != null) {
			// Retrieve list of phones numbers 	
			smslist = daoService.getListNumErreur(msgId, app, list);
		}
		
		Set<String> nums = new HashSet<String>();
		for (Sms sms : smslist) nums.add(sms.getPhone());
		return nums;
	}

	//////////////////////////////////////////////////////////////
	// WS SendSms methods
	//////////////////////////////////////////////////////////////
	/**
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public Boolean isQuotaOk(final Integer nbDest, final String labelAccount) 
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {
		Boolean retVal = sendSmsManager.isQuotaOk(nbDest, labelAccount);
		return retVal;
	}

	/**
	 * @param msgId 
	 * @param perId 
	 * @param bgrId 
	 * @param svcId 
	 * @param smsPhone 
	 * @param labelAccount 
	 * @param msgContent 
	 * @throws UnknownIdentifierApplicationException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	public void sendSMS(final Integer msgId, final Integer perId, final Integer bgrId, 
			final Integer svcId, final String smsPhone, 
			final String labelAccount, final String msgContent) {
		sendSmsManager.sendSMS(msgId, perId, bgrId, svcId, smsPhone, labelAccount, msgContent);	
	}

	//////////////////////////////////////////////////////////////
	// WS SendSmsThird methods
	//////////////////////////////////////////////////////////////
	/**
	 * @throws InsufficientQuotaException 
	 * @see org.esupportail.smsuapi.services.remote.SendSms#snrdSMS()
	 */
	public void sendSMSByThird(final List<String> smsPhoneList, final String msgContent, final int msgId)
	throws UnknownIdentifierApplicationException, 
	InsufficientQuotaException {

		sendSmsThirdManager.sendSMSByThird(smsPhoneList, msgContent, msgId);
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
		if (logger.isDebugEnabled()) {
			logger.debug("Request in domaineService : " + phoneNumber);
		}
		Boolean retVal = blackListManager.isPhoneNumberInBlackList(phoneNumber); 
		if (logger.isDebugEnabled()) {
			logger.debug("Response return in domaineService for : " 
					+ phoneNumber + " is : " + retVal);
		}

		return retVal;
	}

	/**
	 * Test if a phone number is already in the black list.
	 * @param phoneNumber
	 * @return return true if the phone number is in the bl, false otherwise
	 */
	public Set<String> getListPhoneNumbersInBlackList() {
		if (logger.isDebugEnabled()) {
			logger.debug("Request in domainService for ListPhoneNumbersInBlackList");
		}
		Set<String> listPhoneNumbersInBlackList = blackListManager.getListPhoneNumbersInBlackList();

		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(500);
			sb.append("Response in domainService for ListPhoneNumbersInBlackList :");
			for (String nb : listPhoneNumbersInBlackList) {
				sb.append(" - phone number in blacklist = ").append(nb);	
			}
			logger.debug(sb.toString());
		}
		return listPhoneNumbersInBlackList;
	}




	/**
	 * @see org.esupportail.smsuapi.domain.DomainService#getApplicationByName(java.lang.String)
	 */
	public Application getApplicationByName(final String name) {
		return daoService.getApplicationByName(name);
	}


	/////////////////////////////////////////////////////////////
	// WS Reporting method
	//////////////////////////////////////////////////////////////
	/**
	 * get Stats interface.
	 * @param month
	 * @param year
	 */
	public ReportingInfos getStats(final int month, final int year) 
	throws UnknownMonthIndexException, UnknownIdentifierApplicationException {
		ReportingInfos reportingInfos = reportingManager.getStats(month, year);
		return reportingInfos;
	}

	/**
	 * @see org.esupportail.smsuapi.domain.DomainService#testConnexion()
	 */
	public String testConnexion() {

		String sReturn = "Application reconnue : ";
		String application = clientManager.getClientName();

		return sReturn + application;
	}

	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////
	public void setSendSmsManager(final SendSmsManager sendSmsManager) {
		this.sendSmsManager = sendSmsManager;
	}

	public void setSendSmsThirdManager(final SendSmsThirdManager sendSmsThirdManager) {
		this.sendSmsThirdManager = sendSmsThirdManager;
	}

	public void setBlackListManager(final BlackListManager blackListManager) {
		this.blackListManager = blackListManager;
	}

	public void setReportingManager(final ReportingManager reportingManager) {
		this.reportingManager = reportingManager;
	}

	public void setClientManager(final ClientManager clientManager) {
		this.clientManager = clientManager;
	}

}
