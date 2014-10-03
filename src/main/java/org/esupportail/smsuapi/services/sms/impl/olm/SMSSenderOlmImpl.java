package org.esupportail.smsuapi.services.sms.impl.olm;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;

import fr.cvf.util.mgs.message.NotificationLevel;
import fr.cvf.util.mgs.mode.sgs.impl.message.request.RequestFactory;
import fr.cvf.util.mgs.mode.sgs.message.request.SMText;

/**
 * Olm implementation of the SMS sender.
 * @author PRQD8824
 *
 */
public class SMSSenderOlmImpl implements ISMSSender {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * SMS notification level.
	 */
	private static final int NOTIFICATION_LEVEL = NotificationLevel.FINAL;
	
	/**
	 * Olm connector used to send message.
	 */
	private OlmConnector olmConnector;
	
	/**
	 * use to simulate sending.
	 */
	private boolean simulateMessageSending;
	

	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.services.sms.ISMSSender
	 * #sendMessage(org.esupportail.smsuapi.domain.beans.sms.SMSMessage)
	 */
	public void sendMessage(final SMSBroker sms) {
		
		final int smsId = sms.getId();
		final String smsRecipient = sms.getRecipient();
		final String smsMessage = sms.getMessage();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Entering into send message with parameter : ");
			logger.debug("   - message id : " + smsId);
			logger.debug("   - message recipient : " + smsRecipient);
			logger.debug("   - message : " + smsMessage);
		}
		
		try {
			final String messageISOLatin = new String(smsMessage.getBytes(),"ISO-8859-1");
			if (logger.isDebugEnabled()) {
				logger.debug("sending encoded message : " + messageISOLatin);
			}

			final SMText smText = RequestFactory.createSMText(smsId, smsRecipient, messageISOLatin);
			smText.setNotificationLevel(NOTIFICATION_LEVEL);
			
			// only send the message if required
			if (!simulateMessageSending) {
				olmConnector.submit(smText);
				if (logger.isDebugEnabled()) {
					logger.debug("message with : " + 
						  " - id : " + smsId + "successfully sent");
				}
			} else {
				logger.warn("Message with id : " + smsId + " not sent because simlation mode is enable");
			}

			
		} catch (Throwable t) {
			logger.error("An error occurs sending SMS : " + 
				     " - id : " + smsId + 
				     " - recipient : " + smsRecipient + 
				     " - message : " + smsMessage);
		}		

	}
	
	
	/*******************
	 * Mutator.
	 *******************/

	/**
	 * Standard setter used by Spring.
	 */
	public void setOlmConnector(final OlmConnector olmConnector) {
		this.olmConnector = olmConnector;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param simulateMessageSending
	 */
	public void setSimulateMessageSending(final boolean simulateMessageSending) {
		this.simulateMessageSending = simulateMessageSending;
	}
}
