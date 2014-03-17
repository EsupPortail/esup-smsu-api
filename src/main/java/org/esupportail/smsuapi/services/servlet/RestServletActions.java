package org.esupportail.smsuapi.services.servlet;


import javax.servlet.http.HttpServletRequest;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierMessageException;
import org.springframework.beans.factory.annotation.Autowired;

public class RestServletActions {

	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());

	@Autowired private org.esupportail.smsuapi.business.ClientManager clientManager;
	@Autowired private org.esupportail.smsuapi.services.remote.SendSms sendSms;
	@Autowired private org.esupportail.smsuapi.services.remote.SendTrack sendTrack;
	@Autowired private org.esupportail.smsuapi.services.remote.NotificationPhoneNumberInBlackList notificationPhoneNumberInBlackList;

    	public Object wsActionSendSms(HttpServletRequest req) throws UnknownIdentifierApplicationException, InsufficientQuotaException {
		sendSMS(getInteger(req, "id", null),
			getInteger(req, "senderId", null),
			getString(req, "phoneNumber"),
			getString(req, "account", null), 
			getString(req, "message"));
		return "OK";
	}

	/**
	   Since SendSms.sendSMS silently fail,
	   and since we do not want to modify SOAP, we behave differently in REST: we do check first.
	 */
	private void sendSMS(Integer msgId, Integer perId, 
			     String smsPhone, String labelAccount, String msgContent) throws UnknownIdentifierApplicationException, InsufficientQuotaException {
		sendSms.mayCreateAccountCheckQuotaOk(1, labelAccount);
		sendSms.sendSMS(msgId, perId, null, null, smsPhone, labelAccount, msgContent);
	}

    	public Object wsActionIsBlacklisted(HttpServletRequest req) {    
		return notificationPhoneNumberInBlackList
		    .isPhoneNumberInBlackList(getString(req, "phoneNumber"));
	}

    	public Object wsActionMessageInfos(HttpServletRequest req) throws UnknownIdentifierApplicationException, UnknownIdentifierMessageException {    
		return sendTrack.getTrackInfos(getInteger(req, "id"));
	}

	boolean isAuthValid() {
		return clientManager.getApplicationOrNull() != null;
	}


	static String getString(HttpServletRequest req, String name) {
		String val = getString(req, name, null);
		if (val == null) throw new RuntimeException("\"" + name + "\" parameter is mandatory");
		return val;

	}
	static String getString(HttpServletRequest req, String name, String defaultValue) {
		String val = req.getParameter(name);
		return val != null ? val : defaultValue;
	}

	static Integer getInteger(HttpServletRequest req, String name) {
		return Integer.parseInt(getString(req, name));
	}
	static Integer getInteger(HttpServletRequest req, String name, Integer defaultValue) {
		String s = getString(req, name, null);
		return s != null ? Integer.valueOf(s) : defaultValue;
	}

	/**
	 * Standard setter used by spring.
	 */
	public void setClientManager(final org.esupportail.smsuapi.business.ClientManager clientManager) {
		this.clientManager = clientManager;
	}

	/**
	 * Standard setter used by spring.
	 */
	public void setSendSms(final org.esupportail.smsuapi.services.remote.SendSms sendSms) {
		this.sendSms = sendSms;
	}

	/**
	 * Standard setter used by spring.
	 */
	public void setSendTrack(final org.esupportail.smsuapi.services.remote.SendTrack sendTrack) {
		this.sendTrack = sendTrack;
	}

	/**
	 * Standard setter used by spring.
	 */
	public void setNotificationPhoneNumberInBlackList(final org.esupportail.smsuapi.services.remote.NotificationPhoneNumberInBlackList notificationPhoneNumberInBlackList) {
		this.notificationPhoneNumberInBlackList = notificationPhoneNumberInBlackList;
	}

}
