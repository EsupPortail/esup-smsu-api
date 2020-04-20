package org.esupportail.smsuapi.services.servlet;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.services.sms.IBackChannelAck;

/**
 * Servlet used to receive back-channel acks. 
 * Try the various configured brokers.
 */
public class BackChannelAckServlet implements org.springframework.web.HttpRequestHandler {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject List<IBackChannelAck> mayAcks;

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<String> errs = new LinkedList<>();

        for (IBackChannelAck mayAck : mayAcks) {
            try {
                mayAck.mayHandleRequest(req, resp);
                return;
            } catch (InvalidParameterException e) {
                errs.add(e.getMessage());
            }
        }
        // all failed
        logger.error(String.join(", ", errs));
    }
}