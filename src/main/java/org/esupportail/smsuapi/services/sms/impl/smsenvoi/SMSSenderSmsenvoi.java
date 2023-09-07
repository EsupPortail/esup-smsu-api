package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.esupportail.smsuapi.utils.HttpUtils;
import static org.esupportail.smsuapi.utils.Various.firstNonNull;

/**
 * Smsenvoi.com sender.
 */
public class SMSSenderSmsenvoi implements ISMSSender {

    protected final Logger logger = Logger.getLogger(getClass());

    protected boolean simulateMessageSending;
    private String user_key;
    private String access_token;

    protected String wsUrl;

    private JsonNode from;

    @Inject
    private DaoService daoService;
    protected RestTemplate restTemplate;

    public void sendMessage(final SMSBroker sms) {
        sendMessage(sms, null, null);
    }

    public synchronized void sendMessage(final SMSBroker sms, String force_user_key, String force_access_token) {
        try {
            if (logger.isDebugEnabled()) {
                final String messageISOLatin = new String(sms.message.getBytes(), "ISO-8859-1");
                logger.debug("sending encoded message : " + messageISOLatin);
            }

            // using the id of the first SMS in our db
            String broker_id = "" + sms.rcpts.get(0).id;

            SmsenvoiRequest req = new SmsenvoiRequest(sms.message, sms.rcpts, computeSenderlabel(sms), broker_id);

            HttpHeaders requestHeaders = new HttpHeaders();	
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("user_key", firstNonNull(force_user_key, user_key));
            requestHeaders.add("Access_token", firstNonNull(force_access_token, access_token));

            SmsenvoiResponse resp;
            ResponseEntity<SmsenvoiResponse> respEntity = null;
            if (simulateMessageSending) {
                resp = new SmsenvoiResponse();
                resp.result = "OK";
            } else {
                // restTemplate will use here jacksonHttpMessageConverter for response json->object
                respEntity = restTemplate.exchange(
                        wsUrl, HttpMethod.POST, new HttpEntity<>(req, requestHeaders),
                        SmsenvoiResponse.class);
                resp = respEntity.getBody();
            }
            if ("OK".equals(resp.result)) {
                save_message_id(sms, broker_id);
            } else {
                logger.error("smsenvoi response " + respEntity.toString());
            }
        } catch (Throwable t) {
            logger.error("An error occurs sending SMS " + sms.message, t);
            for (SMSBroker.Rcpt s : sms.rcpts) {
                logger.error(" to " + s.id + " - recipient : " + s.recipient + " - message : " + sms.message);
            }
        }

    }

    private void save_message_id(SMSBroker sms, String broker_id) {
        for (SMSBroker.Rcpt s : sms.rcpts) {
            Sms smsDB = daoService.getSms(s.id);
            smsDB.setBrokerId(BackChannelAck.broker_id(broker_id, international_phone_number(s.recipient)));
            daoService.updateSms(smsDB);
        }
    }

    private String international_phone_number(String s) {
        return s.replaceFirst("^0", "+33").replace(" ", "");
    }

    public static String computeSenderlabel(Logger logger, JsonNode from, String accountLabel) {
        String label = from.path(accountLabel).textValue();
        if (label == null) {
            label = from.path("").textValue();
            if (label == null)
                logger.info("no default senderlabel (cf sms.connector.smsenvoi.from.mapJSON), no sender label will be used");
            else
                logger.debug("no senderlabel for " + accountLabel + " in " + from + ". Defaulting to " + label);
        }
        logger.debug("senderlabel: " + label);
        return label == null ? null : (String) label;
    }

    private String computeSenderlabel(SMSBroker sms) {
        return computeSenderlabel(logger, from, sms.accountLabel);
    }

    @Required
    public void setSimulateMessageSending(final boolean simulateMessageSending) {
        this.simulateMessageSending = simulateMessageSending;
    }

    @Required
    public void setSendsms_url(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    @Required
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setFrom_mapJSON(String from_mapJSON) {
        this.from = HttpUtils.json_decode(from_mapJSON);
        if (this.from == null)
            logger.error("invalid from_mapJSON: " + from_mapJSON);
    }

    @Required
    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    @Required
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

}
