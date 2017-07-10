package org.esupportail.smsuapi.domain.beans.sms;

import java.io.Serializable;
import java.util.List;

public class SMSBroker implements Serializable {

	private static final long serialVersionUID = 1L;

    static public class Rcpt implements Serializable {
	   private static final long serialVersionUID = 1L;
	   public int id;
	   public String recipient;
       public Rcpt(int id, String recipient) {
		this.id = id;
		this.recipient = recipient;
	}
	}

	public List<Rcpt> rcpts;
	public String message;
	public String accountLabel;

	public SMSBroker(List<Rcpt> rcpts, final String message, final String accountLabel) {
		this.rcpts = rcpts;
		this.message  = message;
		this.accountLabel = accountLabel;
	}
}
