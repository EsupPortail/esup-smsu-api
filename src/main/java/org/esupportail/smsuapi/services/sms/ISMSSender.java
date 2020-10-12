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
	void sendMessage(SMSBroker smsMessage, String force_login, String force_password);

	default String getId() {
	    return getClass().getPackage().getName().replaceFirst(".*\\.", "");
	}
}
