package org.esupportail.ws.remote.beans;

import java.io.Serializable;

/**
 * The class that represents accounts.
 */
public class MsgIdAndPhone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7187358986757940568L;

	/**
	 * message Id
	 */
	private java.lang.Integer msgId;

	/**
	 * message phone number
	 */
	private java.lang.String phoneNumber;
	
	/**
	 * Bean constructor.
	 */
	public MsgIdAndPhone() {
		super();
	}

	public MsgIdAndPhone(final java.lang.Integer smsId, final java.lang.String phoneNumber) {
		super();
		this.setMsgId(smsId);
		this.setPhoneNumber(phoneNumber);
	}

	/** 
	 * @param smsId the msgId to set
	 */
	public void setMsgId(final Integer smsId) {
		this.msgId = smsId;
	}

	/**
	 * @return the msgId
	 */
	public Integer getMsgId() {
		return msgId;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(final java.lang.String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public java.lang.String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof MsgIdAndPhone)) {
			return false;
		} else {
			MsgIdAndPhone m = (MsgIdAndPhone) obj;
			if (null == this.getMsgId() || null == m.getMsgId())
				return false;
			if (null == this.getPhoneNumber() || null == m.getPhoneNumber())
				return false;
			if (!this.getMsgId().equals(m.getMsgId()))
				return false;
			if (!this.getPhoneNumber().equals(m.getPhoneNumber()))
				return false;
			return true;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MsgIdAndPhone#" + hashCode() + "[msgId=[" + msgId + "], [phoneNumber=[" + phoneNumber + "]]";
	}


}