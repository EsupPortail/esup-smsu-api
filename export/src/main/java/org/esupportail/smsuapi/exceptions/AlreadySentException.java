package org.esupportail.smsuapi.exceptions;

@SuppressWarnings("serial")
public class AlreadySentException extends RuntimeException {

    public AlreadySentException(String err) {
        super(err);
    }
}
