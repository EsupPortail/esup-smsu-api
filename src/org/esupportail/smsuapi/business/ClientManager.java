package org.esupportail.smsuapi.business;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import java.security.cert.X509Certificate;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.esupportail.commons.utils.HttpUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author xphp8691
 *
 */
public class ClientManager implements InitializingBean {

	/**
	 * pattern as sting used to extract the client name.
	 */
	private String subjectDNRegex;
	
	/**
	 * 
	 */
	private DaoService daoService;

	private String PASSWORD_PREFIX_IN_CERTIFCATE = "PASSWORD:";
	
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


	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws IllegalArgumentException {	
		compileSubjectDNRegex();
	}

	public Pattern compileSubjectDNRegex() throws IllegalArgumentException {
		try {
			return Pattern.compile(subjectDNRegex, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			throw new IllegalArgumentException("Malformed regular expression: " + subjectDNRegex);
		}
	}

	public String getNoCertificateNorBasicAuthErrorMessage() {
		return "no certificate nor basic auth received by smsuapi. Either fix smsuapi configuration: you need clientAuth=optional in server.xml (tomcat) or \"SSLVerifyClient optional\" in apache conf (if you use a frontal apache). Or use Basic Auth";
	}

	/**
	 * @return the client name, "unknown" if the name can not be found.
	 * @throws IllegalArgumentException
	 */
	public String getClientName() throws IllegalArgumentException {
		String cn = getCertificateCN();
		if (cn == null) cn = getBasicAuthUser();
		if (cn == null) { 
			logger.error(getNoCertificateNorBasicAuthErrorMessage());
			return "";
		} else {
			return cn;
		}
	}

	private String getCertificateCN() throws IllegalArgumentException {
		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. get Client Name.");
		}
		
		X509Certificate[] certs = getClientX509Certificates();
		
		if ((certs != null) && (certs.length > 0)) {
			return getCNFromCertificate(certs[0]);
		} else {
			return null;
		}
	}

	private X509Certificate[] getClientX509Certificates() {
		HttpServletRequest request = HttpUtils.getHttpServletRequest();
		return (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
	}
	
	/**
	 * 
	 * @param certificateCN
	 * @return
	 */
	public Application getApplicationByCertificateCN(final String certificateCN) {
		if (certificateCN == null) return null;

		for (Application application : daoService.getAllApplications()) {
			try {
				String cn = getCNFromApplication(application);
				if (certificateCN.equalsIgnoreCase(cn)) {					
					if (logger.isDebugEnabled())
						logger.debug("CN in db : [" + cn + "] matches with CN in request : [" + certificateCN + "]");
					return application;
				} else {
					if (logger.isDebugEnabled())
						logger.debug("CN in db : [" + cn + "] does not match with CN in request : [" + certificateCN + "]");
				}
			} catch (CertificateException e) {
				logger.warn("An error occurs getting the certificate from db for application with : \n" + 
					    " - id : " + application.getId(), e);
			}
		}
		return null;
	}
	
	public Application getApplication() throws UnknownIdentifierApplicationException {
		String cn = getCertificateCN();
		if (cn != null) {
			Application app = getApplicationByCertificateCN(cn);
			if (app == null)
				throw new UnknownIdentifierApplicationException("Unknown application " + getClientName() + " or invalid password");
			return app;
		}

		String[] basicAuth = getBasicAuth();
		if (basicAuth != null) 
			return getApplicationByBasicAuth(basicAuth);
		
		throw new UnknownIdentifierApplicationException(getNoCertificateNorBasicAuthErrorMessage());
	}

	public Application getApplicationOrNull() {
		try {
			return getApplication();
		} catch (UnknownIdentifierApplicationException e) {
			logger.error("" + e);
			return null;
		}
	}

	private String getCNFromApplication(Application application) throws CertificateException {
		byte[] certAsByteArray = application.getCertifcate();
		return getCNFromCertificate(
			javax.security.cert.X509Certificate.getInstance(certAsByteArray)
			);
	}
	
	/**
	 * Extract the CN from the X509 certificate.
	 * @param certificate
	 * @return
	 */
	private String getCNFromCertificate(final X509Certificate certificate) {
		String subjectDN = certificate.getSubjectDN().getName();
		return getCNFromSubjectDN(subjectDN);
	}
	private String getCNFromCertificate(final javax.security.cert.X509Certificate certificate) {
		String subjectDN = certificate.getSubjectDN().getName();
		return getCNFromSubjectDN(subjectDN);
	}

	final String getCNFromSubjectDN(final String subjectDN) {
		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. SubjectDN = " + subjectDN);
		}
		Matcher matcher = compileSubjectDNRegex().matcher(subjectDN);
		if (!matcher.find()) 
			return "unknown";
		if (matcher.groupCount() != 1)
			throw new IllegalArgumentException("Regular expression must contain a single group ");

		String name = matcher.group(1);
		logger.debug("Client connexion. name = " + name);
		return name;
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
		if (wantedPassword == null) {
			throw new UnknownIdentifierApplicationException("application " + app.getName() + " must be used using certificate authentication");
		} else if (password.equals(wantedPassword)) {
			return app;
		} else {
			throw new UnknownIdentifierApplicationException("invalid password for application " + app.getName());
		}
	}

	private String getPassword(Application app) {
		return removePrefixOrNull(new String(app.getCertifcate()), PASSWORD_PREFIX_IN_CERTIFCATE);
	}

	private String removePrefixOrNull(String s, String prefix) {
		return s.startsWith(prefix) ? s.substring(prefix.length()) : null;
	}

	private String[] getBasicAuth() {
		HttpServletRequest request = HttpUtils.getHttpServletRequest();
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
	 * @param subjectDNRegex
	 */
	public void setsubjectDNRegex(final String subjectDNRegex) {
		this.subjectDNRegex = subjectDNRegex;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}


}
