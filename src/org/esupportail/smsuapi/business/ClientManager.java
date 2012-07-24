package org.esupportail.smsuapi.business;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import java.security.cert.X509Certificate;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletRequest;

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

	public String getNoCertificateErrorMessage() {
		return "no certificate received by smsuapi. Fix smsuapi configuration: you need clientAuth=true in server.xml (tomcat) or \"SSLVerifyClient require\" in apache conf (if you use a frontal apache)";
	}

	/**
	 * @return the client name, "unknown" if the name can not be found.
	 * @throws IllegalArgumentException
	 */
	public String getClientName() throws IllegalArgumentException {
		return getCertificateCN();
	}

	private String getCertificateCN() throws IllegalArgumentException {
		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. get Client Name.");
		}
		
		X509Certificate[] certs = getClientX509Certificates();
		
		if ((certs != null) && (certs.length > 0)) {
			return getCNFromCertificate(certs[0]);
		} else {
			logger.error(getNoCertificateErrorMessage());
			return "";
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
	
	public Application getApplicationOrNull() {
		return getApplicationByCertificateCN(getCertificateCN());
	}

	public Application getApplication() throws UnknownIdentifierApplicationException {
		Application app = getApplicationOrNull();
		if (app == null)
			throw new UnknownIdentifierApplicationException("Unknown application " + getClientName());
		return app;
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
