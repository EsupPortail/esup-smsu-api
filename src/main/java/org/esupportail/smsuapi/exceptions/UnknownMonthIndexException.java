package org.esupportail.smsuapi.exceptions;

	/**
	 * A class for identifer applica exceptions.
	 */
	public class UnknownMonthIndexException extends Exception {
		
			/**
			 * The id for serialization.
			 */
			private static final long serialVersionUID = 8197090501242229324L;

			/**
			 * @param message
			 */
			public UnknownMonthIndexException(final String message) {
				super(message);
			}

			/**
			 * @param cause
			 */
			public UnknownMonthIndexException(final Throwable cause) {
				super(cause);
			}

			/**
			 * @param message
			 * @param cause
			 */
			public UnknownMonthIndexException(final String message, final Throwable cause) {
				super(message, cause);
			}
		
	}
