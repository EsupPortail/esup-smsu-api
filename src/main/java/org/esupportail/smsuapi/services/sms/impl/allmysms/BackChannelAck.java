package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;

import org.apache.log4j.Logger;
import javax.inject.Inject;

public class BackChannelAck implements org.springframework.web.HttpRequestHandler {

    @Inject
    private DaoService daoService;

    private final Logger logger = Logger.getLogger(getClass());

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String smsId = req.getParameter("smsId");
        if (smsId == null) {
            logger.error("back-channel ack: no smsId !?");
            return;
        }
        Sms sms = daoService.getSmsByBrokerId(smsId);
        if (sms == null) {
            logger.error("back-channel ack: unknown smsId " + smsId);
        } else {
            sms.setStateAsEnum(getStatus(req));
            try {
                sms.setAckDate(parseReceptionDate(req.getParameter("receptionDate"))); // update acknowledge date (we could use "receptionDate" param, but it should be ok)
            } catch (ParseException e) {
                logger.error("back-channel ack: invalid date " + req.getParameter("receptionDate"));
            }
            daoService.updateSms(sms);
        }
        // NB: returning "OK" even if smsId not found in DB: Useful when subscribing the webhook
        resp.getWriter().write("OK");
    }

    private SmsStatus getStatus(HttpServletRequest req) {
        String status = req.getParameter("status");
        switch (status) {
        case "1":
            return SmsStatus.DELIVERED;
        default:
            logger.info("back channel ack: " + req.getParameter("phoneNumber") + " failed:" + status + " "
                    + req.getParameter("statusText"));
            return SmsStatus.ERROR;
        }
    }

    static Date parseReceptionDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).parse(date);
    }

}
