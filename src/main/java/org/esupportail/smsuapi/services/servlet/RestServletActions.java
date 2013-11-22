package org.esupportail.smsuapi.services.servlet;

import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.NotNull;
import org.esupportail.commons.utils.AnnotationsChecker;
import org.esupportail.commons.utils.ReflectionGetters;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierMessageException;
import org.springframework.beans.factory.InitializingBean;

public class RestServletActions implements InitializingBean, ReflectionGetters {

	private final Logger logger = new LoggerImpl(getClass());

	@NotNull private org.esupportail.smsuapi.business.ClientManager clientManager;
	@NotNull private org.esupportail.smsuapi.services.remote.SendSms sendSms;
	@NotNull private org.esupportail.smsuapi.services.remote.SendTrack sendTrack;
	@NotNull private org.esupportail.smsuapi.services.remote.NotificationPhoneNumberInBlackList notificationPhoneNumberInBlackList;

	public void afterPropertiesSet() {
		AnnotationsChecker.check(this);
	}

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
		return val != null ? urldecode(val) : defaultValue;
	}

	static Integer getInteger(HttpServletRequest req, String name) {
		return Integer.parseInt(getString(req, name));
	}
	static Integer getInteger(HttpServletRequest req, String name, Integer defaultValue) {
		String s = getString(req, name, null);
		return s != null ? Integer.valueOf(s) : defaultValue;
	}

	static String urldecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException("urldecode failed on '" + s + "'");
		}
	}


	/**
	 * Automatic getter used by AnnotationsChecker
	 *  implement ReflectionGetters instead of creating getters for all properties for AnnotationsChecker
	 */
	public Object getSimpleProperty(String name) throws Exception { 
		return this.getClass().getDeclaredField(name).get(this);
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
