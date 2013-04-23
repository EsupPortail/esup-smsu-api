package org.esupportail.smsuapi.domain.beans.sms;

import java.io.Serializable;

/**
 * Defines a SMS.
 * @author PRQD8824
 *
 */
public class SMSBroker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identifier message.
	 */
	private int id;
	
	/**
	 * The message recipient.
	 */
	private String recipient;
	
	/**
	 * The message itself.
	 */
	private String message;
	
	private String accountLabel;

	/**
	 * Constructor.
	 */
	public SMSBroker() {
		super();
	}

	public SMSBroker(final int id, final String recipient, final String message, final String accountLabel) {
		super();
		this.id = id;
		this.recipient = recipient;
		this.message  = message;
		this.accountLabel = accountLabel;
	}

	public int getId() {
		return id;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getMessage() {
		return message;
	}

	public String getAccountLabel() {
		return accountLabel;
	}
	
}
