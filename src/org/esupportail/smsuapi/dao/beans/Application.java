package org.esupportail.smsuapi.dao.beans;

import java.io.Serializable;


/**
 * The class that represents applications.
 */
public class Application  implements Serializable {

	/**
	 * Hibernate reference for application.
	 */
	public static final String REF = "Application";

	/**
	 * Hibernate property for the quota.
	 */
	public static final String PROP_QUOTA = "Quota";

	/**
	 * Hibernate property for the institution.
	 */
	public static final String PROP_INS = "Ins";

	/**
	 * Hibernate property for the account.
	 */
	public static final String PROP_ACC = "Acc";

	/**
	 * Hibernate property for the certificate.
	 */
	public static final String PROP_CERTIFCATE = "Certifcate";

	/**
	 * Hibernate property for the number of consumed sms.
	 */
	public static final String PROP_CONSUMED_SMS = "ConsumedSms";

	/**
	 * Hibernate property for the name.
	 */
	public static final String PROP_NAME = "Name";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 7265005139032030997L;

	/**
	 * Application identifier.
	 */
	private java.lang.Integer id;

	/**
	 * Application name.
	 */
	private java.lang.String name;

	/**
	 * Application certificate.
	 */
	private byte[] certifcate;

	/**
	 * Application quota.
	 */
	private java.lang.Long quota;

	/**
	 * Number of consumed sms by application.
	 */
	private java.lang.Long consumedSms;

	/**
	 * Application Account.
	 */
	private Account acc;

	/**
	 * Application Institution.
	 */
	private Institution ins;

	/**
	 * Collection of SMS associated to the application.
	 */
	private java.util.Set<Sms> sms;
	
	/**
	 * Collection of phone number in blacklist.
	 */
	private java.util.Set<Blacklist> blacklists;

	/**
	 * Bean constructor.
	 */
	public Application() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Application(
			final java.lang.Integer id,
			final Account acc,
			final Institution ins,
			final java.lang.String name,
			final java.lang.Long quota,
			final java.lang.Long consumedSms) {

		this.setId(id);
		this.setAcc(acc);
		this.setIns(ins);
		this.setName(name);
		this.setQuota(quota);
		this.setConsumedSms(consumedSms);
	}





	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="APP_ID"
     */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class.
	 * @param id the new ID
	 */
	public void setId(final java.lang.Integer id) {
		this.id = id;
	}


	/**
	 * Return the value associated with the column: APP_NAME.
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Set the value related to the column: APP_NAME.
	 * @param name the APP_NAME value
	 */
	public void setName(final java.lang.String name) {
		this.name = name;
	}



	/**
	 * Return the value associated with the column: APP_CERTIFCATE.
	 */
	public byte[] getCertifcate() {
		return certifcate;
	}

	/**
	 * Set the value related to the column: APP_CERTIFCATE.
	 * @param certifcate the APP_CERTIFCATE value
	 */
	public void setCertifcate(final byte[] certifcate) {
		this.certifcate = certifcate;
	}



	/**
	 * Return the value associated with the column: APP_QUOTA.
	 */
	public java.lang.Long getQuota() {
		return quota;
	}

	/**
	 * Set the value related to the column: APP_QUOTA.
	 * @param quota the APP_QUOTA value
	 */
	public void setQuota(final java.lang.Long quota) {
		this.quota = quota;
	}



	/**
	 * Return the value associated with the column: APP_CONSUMED_SMS.
	 */
	public java.lang.Long getConsumedSms() {
		return consumedSms;
	}

	/**
	 * Set the value related to the column: APP_CONSUMED_SMS.
	 * @param consumedSms the APP_CONSUMED_SMS value
	 */
	public void setConsumedSms(final java.lang.Long consumedSms) {
		this.consumedSms = consumedSms;
	}



	/**
	 * Return the value associated with the column: ACC_ID.
	 */
	public Account getAcc() {
		return acc;
	}

	/**
	 * Set the value related to the column: ACC_ID.
	 * @param acc the ACC_ID value
	 */
	public void setAcc(final Account acc) {
		this.acc = acc;
	}



	/**
	 * Return the value associated with the column: INS_ID.
	 */
	public Institution getIns() {
		return ins;
	}

	/**
	 * Set the value related to the column: INS_ID.
	 * @param ins the INS_ID value
	 */
	public void setIns(final Institution ins) {
		this.ins = ins;
	}



	/**
	 * Return the value associated with the column: Sms.
	 */
	public java.util.Set<Sms> getSms() {
		return sms;
	}

	/**
	 * Set the value related to the column: Sms.
	 * @param sms the Sms value
	 */
	public void setSms(final java.util.Set<Sms> sms) {
		this.sms = sms;
	}

	/**
	 * Add a sms to the collection of sms associated to the application.
	 * @param sms
	 */
	public void addToSms(final Sms sms) {
		if (null == getSms()) {
			setSms(new java.util.TreeSet<Sms>());
		}
		getSms().add(sms);
	}

	/**
	 * Return the value associated with the column: Blacklists.
	 */
	public java.util.Set<Blacklist> getBlacklists() {
		return blacklists;
	}

	/**
	 * Set the value related to the column: Blacklists.
	 * @param blacklists the Blacklists value
	 */
	public void setBlacklists(final java.util.Set<Blacklist> blacklists) {
		this.blacklists = blacklists;
	}

	/**
	 * add a blacklist item to the collection of blacklist associated to the application.
	 * @param blacklist
	 */
	public void addToBlacklists(final Blacklist blacklist) {
		if (null == getBlacklists()) {
			setBlacklists(new java.util.TreeSet<Blacklist>());
		}
		getBlacklists().add(blacklist);
	}




	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Application)) {
			return false;
		} else {
			Application application = (Application) obj;
			if (null == this.getId() || null == application.getId()) {
				return false;
			} else {
				return this.getId().equals(application.getId());
			}
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
		return "Application#" + hashCode() + "[id=[" + id + "], name=[" + name 
		+ "], quota=[" + quota + "], number of consumed sms=[" + consumedSms +  "]]";
	}


}