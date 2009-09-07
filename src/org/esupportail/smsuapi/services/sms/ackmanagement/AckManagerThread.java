package org.esupportail.smsuapi.services.sms.ackmanagement;

import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;
import org.esupportail.smsuapi.business.context.ApplicationContextUtils;

/**
 * Implementation of ack managment by thread.
 * @author PRQD8824
 *
 */
public class AckManagerThread extends Thread {
	
	/**
	 * The ack manager business bean name (in spring context).
	 */
	private static final String ACK_BUSINESS_MGR_BEAN_NAME = "ackManagerBusiness";
	
	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	
	/**
	 * The ack to be managed by this thread.
	 */
	private Acknowledgment acknowledgment;
	
	
	 /* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		
		try {
			ApplicationContextUtils.initApplicationContext();
			// Open connexion & transaction using esup-commons method.
			// open connexion
			DatabaseUtils.open();  
			// open transaction
			DatabaseUtils.begin();


			if (logger.isDebugEnabled()) {
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Starting new Thread to manage ack with : ");
				stringBuilder.append(" - smid : ").append(acknowledgment.getSmsId());
				stringBuilder.append(" - ack status : ").append(acknowledgment.getSmsStatus());
				logger.debug(stringBuilder.toString());
			}

			// Send the ack to the business layer 
			final AckManagerBusiness ackManagerBusiness = (AckManagerBusiness) BeanUtils.getBean(ACK_BUSINESS_MGR_BEAN_NAME);
			ackManagerBusiness.manageAck(acknowledgment);

			// commit transaction
			DatabaseUtils.commit(); 
		// error case, rollback
		} catch (RuntimeException e) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Catching exception in AckManagerThread, now rollback : ");
			logger.error(sb.toString(), e);
			DatabaseUtils.rollback(); 
			throw e;
		// close the session every time	
		} finally {
			DatabaseUtils.close(); 
		}
	 }

	
	/**
	 * Mutator
	 */
	
	/**
	 * 
	 * @param acknowledgment
	 */
	public void setAcknowledgment(final Acknowledgment acknowledgment) {
		this.acknowledgment = acknowledgment;
	}
	
}
