package org.esupportail.smsuapi.business.context;

import javax.servlet.ServletContext;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;

/**
 * Use to manage application context esup-commons way.
 * @author PRQD8824
 *
 */
public class ApplicationContextUtils {

	/**
     * logger.
     */
	private static final Logger logger = new LoggerImpl(org.esupportail.smsuapi.business.context.ApplicationContextUtils.class);
	
	/**
	 * Use to initialise application context. 
	 */
	public static void initApplicationContext() {
		final ServletContext servletContext = SmsuapiServletContextListener.getServletContext();
		if (servletContext != null) {
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder("Initializing application context using servlet context");
				logger.debug(sb.toString());
			}
			BeanUtils.initBeanFactory(servletContext);
		} else {
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder("No specific context found, no specific initialisation done");
				logger.debug(sb.toString());
			}
		}
	}
}
