package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

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

    // 
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Example: ?_dummy=x&recipient=%2B33623456789&delivery_date=20200403190115&order_id=20&status=DLVRD
        String recipient = getParameter(req, "recipient");
        String delivery_date = getParameter(req, "delivery_date");
        String order_id = getParameter(req, "order_id");
        if (recipient == null || delivery_date == null || order_id == null) return;
        Sms sms = daoService.getSmsByBrokerId(broker_id(order_id, recipient));
        if (sms == null) {
            logger.error("back-channel ack: unknown smsId " + broker_id(order_id, recipient));
        } else {
            sms.setStateAsEnum(getStatus(req));
            try {
                sms.setAckDate(parseReceptionDate(delivery_date)); // update acknowledge date (we could use "receptionDate" param, but it should be ok)
            } catch (ParseException e) {
                logger.error("back-channel ack: invalid date " + delivery_date);
            }
            daoService.updateSms(sms);
        }
    }

    private String getParameter(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        if (val == null) logger.error("back-channel ack: no " + name + " !?");
        return val;
    }

    private SmsStatus getStatus(HttpServletRequest req) {
        String status = req.getParameter("status");
        switch (status) {
        case "DLVRD":
            return SmsStatus.DELIVERED;
        default:
            logger.info("back channel ack: " + req.getParameter("recipient") + " failed:" + status + " " + status);
            return SmsStatus.ERROR;
        }
    }

    static Date parseReceptionDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).parse(date);
    }

    static String broker_id(String order_id, String international_phone_number) {
        return order_id + "-" + international_phone_number;
    }

}
