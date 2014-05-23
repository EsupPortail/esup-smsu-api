package org.esupportail.smsuapi.exceptions;

/**
 * A class for identifer applica exceptions.
 */
public class AuthenticationFailed extends RuntimeException {
	
		/**
		 * The id for serialization.
		 */
		private static final long serialVersionUID = 8197090501242229324L;

		/**
		 * @param message
		 */
		public AuthenticationFailed(final String message) {
			super(message);
		}

}
