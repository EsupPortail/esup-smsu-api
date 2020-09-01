package org.esupportail.smsuapi.dao.beans;

import java.io.Serializable;


/**
 * The class that represents institutions.
 */
public class Institution  implements Serializable {

	/**
	 * Hibernate reference for institution.
	 */
	public static final String REF = "Institution";

	/**
	 * Hibernate property for the label.
	 */
	public static final String PROP_LABEL = "Label";

	/**
	 * Hibernate property for the identifier.
	 */
	public static final String PROP_ID = "Id";

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 5548416143260535777L;

	/**
	 * Institution identifier.
	 */
	private java.lang.Integer id;

	/**
	 * Institution label.
	 */
	private java.lang.String label;

	/**
	 * Bean constructor.
	 */
	public Institution() {
		super();
	}

	/**
	 * Constructor for required fields.
	 */
	public Institution(
			final java.lang.Integer id,
			final java.lang.String label) {

		this.setId(id);
		this.setLabel(label);
	}


	/**
	 * Return the unique identifier of this class.
     * @hibernate.id
     *  generator-class="native"
     *  column="INS_ID"
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
	 * Return the value associated with the column: INS_LABEL.
	 */
	public java.lang.String getLabel() {
		return label;
	}

	/**
	 * Set the value related to the column: INS_LABEL.
	 * @param label the INS_LABEL value
	 */
	public void setLabel(final java.lang.String label) {
		this.label = label;
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public boolean equals(final Object obj) {
		if (null == obj) {
			return false;
		}
		if (!(obj instanceof Institution)) {
			return false;
		} else {
			Institution institution = (Institution) obj;
			if (null == this.getId() || null == institution.getId()) {
				return false;
			} else {
				return this.getId().equals(institution.getId());
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
		return "Account#" + hashCode() + "[id=[" + id + "], label=[" + label 
		+  "]]";
	}


}