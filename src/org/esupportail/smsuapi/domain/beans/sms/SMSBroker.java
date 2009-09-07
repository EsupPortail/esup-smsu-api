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
	
	/**
	 * Constructor.
	 */
	public SMSBroker() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(final String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}
	
}
