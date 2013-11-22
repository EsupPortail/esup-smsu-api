package org.esupportail.ws.remote.beans;

import java.io.Serializable;


/**
 * The class that represents accounts.
 */
public class ReportingInfos implements Serializable {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -1278768065587081098L;

	/**
	 * number of SMS.
	 */
	private java.lang.Long nbSms;

	/**
	 * number of SMS in error.
	 */
	private java.lang.Long nbSmsInError;

	/**
	 * account label.
	 */
	private java.lang.String accountLabel;

	/**
	 * month.
	 */
	private int month;
	
	/**
	 * year.
	 */
	private int year;
	
	/**
	 * Bean constructor.
	 */
	public ReportingInfos() {
		super();
	}

	/**
	 * @param nbSms the nbSms to set
	 */
	public void setNbSms(final java.lang.Long nbSms) {
		this.nbSms = nbSms;
	}

	/**
	 * @return the nbSms
	 */
	public java.lang.Long getNbSms() {
		return nbSms;
	}

	/**
	 * @param nbSmsInError the nbSmsInError to set
	 */
	public void setNbSmsInError(final java.lang.Long nbSmsInError) {
		this.nbSmsInError = nbSmsInError;
	}

	/**
	 * @return the nbSmsInError
	 */
	public java.lang.Long getNbSmsInError() {
		return nbSmsInError;
	}

	/**
	 * @param accountLabel the accountLabel to set
	 */
	public void setAccountLabel(final java.lang.String accountLabel) {
		this.accountLabel = accountLabel;
	}

	/**
	 * @return the accountLabel
	 */
	public java.lang.String getAccountLabel() {
		return accountLabel;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	

}