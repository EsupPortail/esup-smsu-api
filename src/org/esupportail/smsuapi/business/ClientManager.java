package org.esupportail.smsuapi.business;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.web.servlet.XFireServletController;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author xphp8691
 *
 */
public class ClientManager implements InitializingBean {

	/**
	 * pattern used to extract the client name.
	 */
	private Pattern subjectDNPattern;

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
		
		Perl5Compiler compiler = new Perl5Compiler();

        try {
            subjectDNPattern = compiler.compile(subjectDNRegex,
                    Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK);
        } catch (MalformedPatternException mpe) {
            throw new IllegalArgumentException("Malformed regular expression: " + subjectDNRegex);
        }

	}

	/**
	 * @return the client name, "unknown" if the name can not be found.
	 * @throws IllegalArgumentException
	 */
	public String getClientName() throws IllegalArgumentException {
		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. get Client Name.");
		}
		
		String name = "";
		HttpServletRequest request = XFireServletController.getRequest();
		
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		
		if ((certs != null) && (certs.length > 0)) {
			final X509Certificate cert = certs[0];
			final String subjectDN  = cert.getSubjectDN().getName();
			name = getCNFromSubjectDN(subjectDN);
		} else {
			logger.error("no certificat found, check config of server.xml (clientAuth=true)");
		}
		
		return name;
	}
	
	/**
	 * 
	 * @param certificateCN
	 * @return
	 */
	public Application getApplicationByCertificateCN(final String certificateCN) {
		Application retVal = null;
		
		if (certificateCN != null) {
			final List<Application> allApplicationList = daoService.getAllApplications();
			final Iterator<Application> allApplicationIte = allApplicationList.iterator();

			boolean found = false;

			while (allApplicationIte.hasNext() && !found) {
				final Application application = allApplicationIte.next();
				final byte[] certAsByteArray = application.getCertifcate();
				try {
					final javax.security.cert.X509Certificate certificate = 
						  javax.security.cert.X509Certificate.getInstance(certAsByteArray);
					final String subjectDN = certificate.getSubjectDN().getName();
					final String cn = getCNFromSubjectDN(subjectDN);

					if (certificateCN.equalsIgnoreCase(cn)) {
						retVal = application;
						found = true;
						
						if (logger.isDebugEnabled()) {
							logger.debug("CN in db : [" + cn + "] matches with CN in request : [" + certificateCN + "]");
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("CN in db : [" + cn + "] does not match with CN in request : [" + certificateCN + "]");
						}
					}

				} catch (CertificateException e) {
					logger.warn("An error occurs getting the certificate from db for application with : \n" + 
						    " - id : " + application.getId(), e);
				}
			}
		}
		return retVal;
		
	}
	
	public Application getApplication() {
		return getApplicationByCertificateCN(getClientName());
	}
	
	/**
	 * Extract the CN from the X509 certificate.
	 * @param certificate
	 * @return
	 */
	final String getCNFromSubjectDN(final String subjectDN) {
		String name = "";
		
		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. SubjectDN = " + subjectDN);
		}

		PatternMatcher matcher = new Perl5Matcher();

		if (!matcher.contains(subjectDN, subjectDNPattern)) {
			name = "unknown";
			// throw new BadCredentialsException(messages.getMessage("DaoX509AuthoritiesPopulator.noMatching",
			// new Object[] {subjectDN}, "No matching pattern was found in subjectDN: {0}"));
		}

		MatchResult match = matcher.getMatch();

		if (match.groups() != 2) { 
			// 2 = 1 + the entire match
			throw new IllegalArgumentException("Regular expression must contain a single group ");
		}

		name = match.group(1);

		if (logger.isDebugEnabled()) {
			logger.debug("Client connexion. name = " + name);
		}
		
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