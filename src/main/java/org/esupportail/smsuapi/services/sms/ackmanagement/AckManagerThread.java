package org.esupportail.smsuapi.services.sms.ackmanagement;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

import org.esupportail.commons.context.ApplicationContextHolder;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.utils.HibernateUtils;

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
	private final Logger logger = Logger.getLogger(getClass());
	
	
	/**
	 * The ack to be managed by this thread.
	 */
	private Acknowledgment acknowledgment;
	
	
	 /* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// need <bean id="..." class="org.esupportail.commons.context.ApplicationContextHolder" />
		ApplicationContext applicationContext = ApplicationContextHolder.getContext();

		SessionFactory sessionFactory = HibernateUtils.getSessionFactory(applicationContext);
		boolean participate = HibernateUtils.openSession(sessionFactory);

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting new Thread to manage ack with : " + 
					     " - smid : " + acknowledgment.getSmsId() + 
					     " - ack status : " + acknowledgment.getSmsStatus());
			}

			// Send the ack to the business layer 
			final AckManagerBusiness ackManagerBusiness = (AckManagerBusiness) applicationContext.getBean(ACK_BUSINESS_MGR_BEAN_NAME);
			ackManagerBusiness.manageAck(acknowledgment);

		// error case, rollback
		} catch (RuntimeException e) {
			logger.error("Catching exception in AckManagerThread, now rollback : ", e);
			throw e;
		// close the session every time	
		} finally {
		    HibernateUtils.closeSession(sessionFactory, participate);
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
