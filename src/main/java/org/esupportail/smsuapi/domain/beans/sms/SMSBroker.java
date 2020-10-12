package org.esupportail.smsuapi.domain.beans.sms;

import java.io.Serializable;
import java.util.List;

import org.esupportail.smsuapi.dao.beans.Account;

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
	public String broker;

	public SMSBroker(List<Rcpt> rcpts, final String message, Account account) {
		this.rcpts = rcpts;
		this.message  = message;
		this.accountLabel = account.getLabel();
		this.broker = account.getBroker();
	}
}
