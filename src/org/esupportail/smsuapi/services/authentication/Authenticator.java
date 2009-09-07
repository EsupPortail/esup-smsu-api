package org.esupportail.smsuapi.services.authentication;

import org.esupportail.smsuapi.domain.beans.User;

/**
 * The interface of authenticators.
 */
public interface Authenticator {

	/**
	 * @return the authenticated user.
	 */
	User getUser();

}