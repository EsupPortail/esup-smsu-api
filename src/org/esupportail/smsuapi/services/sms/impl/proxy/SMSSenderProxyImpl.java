package org.esupportail.smsuapi.services.sms.impl.proxy;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.remote.SendSms;
import org.esupportail.smsuapi.services.sms.ISMSSender;

/**
 * Proxy implementation of the SMS sender.
 * @author prigaux
 *
 */
public class SMSSenderProxyImpl implements ISMSSender {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Proxy connector used to send message.
	 */
	private SendSms sendSms;
	
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
		
		logger.info("Send message to proxy with parameter : ");
		logger.info("   - message id : " + smsId);
		logger.info("   - message recipient : " + smsRecipient);
		logger.info("   - message : " + smsMessage);
		
		try {
			
			// only send the message if required
			if (!simulateMessageSending) {
				sendSms.sendSMS(smsId, null, null, null, smsRecipient, null, smsMessage);

				logger.info("message with : " + 
						  " - id : " + smsId + "successfully sent");
			} else {
				logger.warn("Message with id : " + smsId + " not sent because simlation mode is enable");
			}

			
		} catch (Throwable t) {
			logger.error("An error occurs sending SMS : " + 
				     " - id : " + smsId + 
				     " - recipient : " + smsRecipient + 
				     " - message : " + smsMessage, t);
		}		

	}
	
	
	/*******************
	 * Mutator.
	 *******************/

	/**
	 * Standard setter used by Spring.
	 */
	public void setSendSms(final SendSms sendSms) {
		this.sendSms = sendSms;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param simulateMessageSending
	 */
	public void setSimulateMessageSending(final boolean simulateMessageSending) {
		this.simulateMessageSending = simulateMessageSending;
	}
}
