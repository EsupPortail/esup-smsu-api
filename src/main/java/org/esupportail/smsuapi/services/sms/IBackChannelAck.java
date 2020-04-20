package org.esupportail.smsuapi.services.sms;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for back channel ack implementations
 *
 */
public interface IBackChannelAck {
    void mayHandleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvalidParameterException;
}
