package org.esupportail.smsuapi.services.sms.ackmanagement;

import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;


/**
 * 
 * @author PRQD8824
 *
 */
public class Acknowledgment {

	/**
	 * The sms unique identifier.
	 */
	private int smsId;
	
	/**
	 * the sms ack status.
	 */
	private SmsStatus smsStatus;
	

	public int getSmsId() {
		return smsId;
	}

	public void setSmsId(final int smsId) {
		this.smsId = smsId;
	}

	public SmsStatus getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(final SmsStatus ackStatus) {
		this.smsStatus = ackStatus;
	}
	
}
