package org.esupportail.smsuapi.services.sms.ackmanagement;

/**
 * Common thread launcher for ack managment.
 * Every broker should call this class to manage ack
 * This class should be overwrite if thread pool managment change (ie Qtz case)
 * @author PRQD8824
 *
 */
public class AckManager {

	/**
	 * 
	 * @param acknowledgment
	 */
	public void manageAck(final Acknowledgment acknowledgment) {
		final AckManagerThread ackManagerThread = new AckManagerThread();
		
		ackManagerThread.setAcknowledgment(acknowledgment);
		ackManagerThread.run();
	}
}
