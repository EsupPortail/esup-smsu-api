/**
 * ESUP-Portail esup-smsu-api-admin - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-smsu-api-admin
 */
package org.esupportail.smsuapi.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.smsuapi.business.ClientManager;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.springframework.beans.factory.InitializingBean;



/**
 * The basic implementation of DomainService.
 * 
 * See /properties/domain/domain-example.xml
 */
public class DomainService implements InitializingBean {

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;

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
	public DomainService() {
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
	 * @throws UnknownMessageIdException 
	 */
	public int getNbDest(final Integer msgId) throws UnknownMessageIdException {
		Application app = clientManager.getApplication();
		{
			if (daoService.getNbDest(msgId, app) == 0) {
				throw new UnknownMessageIdException();
			} else {
				return daoService.getNbDest(msgId, app);
			}
		}	
	}

	/**
	 * @return the non-authorized phone numbers (in black list).
	 */
	public int getNbDestBlackList(final Integer msgId) {
		List<String> list = new ArrayList<String>();
		list.add(SmsStatus.ERROR_PRE_BL.name());
		list.add(SmsStatus.ERROR_POST_BL.name());

		Application app = clientManager.getApplication();
		return daoService.getNbSmsWithState(msgId, app, list);


	}

	/**
	 * @return the number of sent SMS.
	 * @throws AuthenticationFailed 
	 */
	public int getNbSentSMS(final Integer msgId) {
		Application app = clientManager.getApplication();
		return daoService.getNbSentSMS(msgId, app);


	}

	/**
	 * @return the number of SMS in progress.
	 * @throws AuthenticationFailed 
	 */
	public int getNbProgressSMS(final Integer msgId) {
		Application app = clientManager.getApplication();
		return daoService.getNbProgressSMS(msgId, app);
	}

	/**
	 * @return the number of SMS in error.
	 * @throws AuthenticationFailed 
	 * @throws Exception 
	 */
	public int getNbErrorSMS(final Integer msgId) {
		List<String> list = new ArrayList<String>();
		list.add(SmsStatus.ERROR.name());
		list.add(SmsStatus.ERROR_PRE_BL.name());
		list.add(SmsStatus.ERROR_POST_BL.name());

		Application app = clientManager.getApplication();
		return daoService.getNbErrorSMS(msgId, app, list);

	}

	/**
	 * @return the number of SMS in error.
	 */
	public Set<String> getListNumErreur(final Integer msgId) {
		// list criteria for HQL query 
		List<String> list = new ArrayList<String>();
		list.add(SmsStatus.ERROR.name());
		list.add(SmsStatus.ERROR_POST_BL.name());
		list.add(SmsStatus.ERROR_PRE_BL.name());

		List<Sms> smslist = new ArrayList<Sms>();

		Application app = clientManager.getApplicationOrNull();

		if (app != null) {
			// Retrieve list of phones numbers 	
			smslist = daoService.getListNumErreur(msgId, app, list);
		}
		
		Set<String> nums = new HashSet<String>();
		for (Sms sms : smslist) nums.add(sms.getPhone());
		return nums;
	}

	public Sms getSms(Integer msgId, String phoneNumber) {
		Application app = clientManager.getApplication();
		
		List<Sms> l = daoService.getSms(app, msgId, phoneNumber);
		if (l.size() == 0) {
			return null;
		} else if (l.size() > 1) {
			logger.error("internal error: more than one sms corresponds to " + msgId + ":" + phoneNumber);
			return null;
		} else {
			return l.get(0);
		}
	}

	/**
	 * @see org.esupportail.smsuapi.domain.DomainService#testConnexion()
	 */
	public String testConnexion() {

		String sReturn = "Application reconnue : ";
		String application = clientManager.getClientName();

		if (application.equals(""))
		    return "ERROR : " + clientManager.getNoBasicAuthErrorMessage();
		else 
		    return sReturn + application;
	}

	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////
	public void setClientManager(final ClientManager clientManager) {
		this.clientManager = clientManager;
	}

}
