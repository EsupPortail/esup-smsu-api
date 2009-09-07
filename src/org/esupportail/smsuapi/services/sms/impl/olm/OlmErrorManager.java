package org.esupportail.smsuapi.services.sms.impl.olm;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

import fr.cvf.util.mgs.ErrorManager;

/**
 * Error manager for olm broker impl.
 * @author PRQD8824
 *
 */
public class OlmErrorManager implements ErrorManager {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	
	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.ErrorManager#error(int, int, java.lang.String)
	 */
	public void error(final int arg0, final int arg1, final String arg2) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Error sent by lib SGS :");
		sb.append(" - arg0 : " + arg0);
		sb.append(" - arg1 : " + arg1);
		sb.append(" - arg2 : " + arg2);
		logger.error(sb.toString());

	}

	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.ErrorManager#warning(java.lang.String)
	 */
	public void warning(final String arg0) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Warning sent by lib SGS :");
		sb.append(" - arg0 : " + arg0);
		logger.warn(sb.toString());
	}
	
	/**
	 * @param xmlFrame
	 * @return
	 */
	public boolean manage(final String xmlFrame) {
		logger.debug("xmlFrame : " + xmlFrame);
		return true;
	}

}
