package org.esupportail.smsuapi.exceptions;

@SuppressWarnings("serial")
public class AlreadySentException extends InvalidParameterException {

    public AlreadySentException(String err) {
        super(err);
    }
}
