package org.esupportail.smsuapi.business.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
* @author PRQD8824
*/
public class SmsuapiServletContextListener implements ServletContextListener {

	// The servlet context
	private static ServletContext servletContex;
	
	/////////////////////
	// Constructors
	/////////////////////
	/**
	 * Bean constructor.
	 */
	public SmsuapiServletContextListener() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		// nothing to do here
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		 servletContex = servletContextEvent.getServletContext();
	}
	
	/**
	 * Return the application servlet context. 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContex;
	}
	
}
