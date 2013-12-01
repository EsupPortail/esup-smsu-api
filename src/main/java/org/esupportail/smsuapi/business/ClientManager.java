package org.esupportail.smsuapi.business;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author xphp8691
 *
 */
public class ClientManager {

	/**
	 * 
	 */
	private DaoService daoService;
	
	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	
	/**
	 * constructor.
	 */
	public ClientManager() {
		super();
	}

	public String getNoBasicAuthErrorMessage() {
		return "no basic auth received by smsuapi. You must use Basic Auth";
	}

	/**
	 * @return the client name, "unknown" if the name can not be found.
	 * @throws IllegalArgumentException
	 */
	public String getClientName() throws IllegalArgumentException {
		String cn = getBasicAuthUser();
		if (cn == null) { 
			logger.error(getNoBasicAuthErrorMessage());
			return "";
		} else {
			return cn;
		}
	}

	private HttpServletRequest getHttpServletRequest() {
		// need <listener> RequestContextListener in web.xml
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	public Application getApplication() throws UnknownIdentifierApplicationException {
		String[] basicAuth = getBasicAuth();
		if (basicAuth != null) 
			return getApplicationByBasicAuth(basicAuth);
		
		throw new UnknownIdentifierApplicationException(getNoBasicAuthErrorMessage());
	}

	public Application getApplicationOrNull() {
		try {
			return getApplication();
		} catch (UnknownIdentifierApplicationException e) {
			logger.error("" + e);
			return null;
		}
	}

	private Application getApplicationByBasicAuth(String[] userAndPassword) throws UnknownIdentifierApplicationException {
		return getApplicationByBasicAuth(userAndPassword[0], userAndPassword[1]);
	}
	private Application getApplicationByBasicAuth(String user, String password) throws UnknownIdentifierApplicationException {
		Application app = daoService.getApplicationByName(user);
		if (app == null) {
			throw new UnknownIdentifierApplicationException("unknown application " + user);
		}
		String wantedPassword = getPassword(app);
		if (password.equals(wantedPassword)) {
			return app;
		} else {
			throw new UnknownIdentifierApplicationException("invalid password for application " + app.getName());
		}
	}

	private String getPassword(Application app) {
		return app.getPassword();
	}

	private String[] getBasicAuth() {
		HttpServletRequest request = getHttpServletRequest();
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null) return null;

		Matcher matcher = Pattern.compile("Basic\\s+(.*)", Pattern.CASE_INSENSITIVE).matcher(authHeader);
		if (!matcher.find()) return null;

		String userPass = decodeBase64(matcher.group(1));
		logger.debug("found basic auth " + userPass);
		return userPass.split(":", 2);
	}

	private String getBasicAuthUser() {
		String[] userAndPassword = getBasicAuth();
		return userAndPassword == null ? null : userAndPassword[0];
	}

	private String decodeBase64(String s) {
		return new String(Base64.decodeBase64(s.getBytes()));
	}

	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}


}
