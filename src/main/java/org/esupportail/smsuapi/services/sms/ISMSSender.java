package org.esupportail.smsuapi.services.sms;

import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;

/**
 * Interface of all SMS Sender implementation.
 * @author PRQD8824
 *
 */
public interface ISMSSender {

	/**
	 * Send the specified message.
	 * @param smsMessage
	 */
	void sendMessage(SMSBroker smsMessage);
	
}
