package org.esupportail.smsuapi.services.sms.impl.allmysms;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.esupportail.smsuapi.services.sms.impl.smsenvoi.SMSSenderSmsenvoiImpl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.esupportail.smsuapi.utils.HttpUtils;

/**
 * Allmysms.com sender.
 */
public class SMSSenderAllmysms implements ISMSSender {

    protected final Logger logger = Logger.getLogger(getClass());

    protected boolean simulateMessageSending;
    private String account_login;
    private String account_apikey;

    protected String wsUrl;
    protected String simulate_wsUrl;

    private JsonNode from;

    @Autowired
    private DaoService daoService;
    protected RestTemplate restTemplate;

    public synchronized void sendMessage(final SMSBroker sms) {
        final int smsId = sms.getId();
        final String smsRecipient = sms.getRecipient();
        final String smsMessage = sms.getMessage();

        if (logger.isDebugEnabled()) {
            logger.debug("Entering into send message with parameter : ");
            logger.debug("   - message id : " + smsId);
            logger.debug("   - message recipient : " + smsRecipient);
            logger.debug("   - message : " + smsMessage);
        }

        try {
            if (logger.isDebugEnabled()) {
                final String messageISOLatin = new String(smsMessage.getBytes(), "ISO-8859-1");
                logger.debug("sending encoded message : " + messageISOLatin);
            }

            AllmysmsRequest req = new AllmysmsRequest(smsMessage, smsRecipient, computeSenderlabel(sms));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("login", account_login);
            params.add("apiKey", account_apikey);
            params.add("smsData", req.toJson());

            // restTemplate will use here jacksonHttpMessageConverter for response json->object
            ResponseEntity<AllmysmsResponse> respEntity = restTemplate.exchange(
                    simulateMessageSending ? simulate_wsUrl : wsUrl, HttpMethod.POST, new HttpEntity<>(params, null),
                    AllmysmsResponse.class);
            AllmysmsResponse resp = respEntity.getBody();
            logger.info("allmysms response " + resp.toString());
            save_message_id(sms, resp);
        } catch (Throwable t) {
            logger.error("An error occurs sending SMS : " +

                    " - id : " + smsId + " - recipient : " + smsRecipient + " - message : " + smsMessage, t);
        }

    }

    private void save_message_id(SMSBroker sms, AllmysmsResponse response) {
        Sms smsDB = daoService.getSms(sms.getId());

        if (response.status == null || response.status != 100) {
            logger.error("allmysend send failed: " + response);
            smsDB.setStateAsEnum(SmsStatus.ERROR);
        }

        if (response.smsIds != null) {
            AllmysmsResponse.SmsId respIds = response.smsIds.get(0);
            smsDB.setBrokerId(respIds.smsId);
        }

        daoService.updateSms(smsDB);
    }

    private String computeSenderlabel(SMSBroker sms) {
        return SMSSenderSmsenvoiImpl.computeSenderlabel(logger, from, sms);
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
    public void setSimulate_sendsms_url(String simulate_wsUrl) {
        this.simulate_wsUrl = simulate_wsUrl;
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
    public void setAccount_login(String account_login) {
        this.account_login = account_login;
    }

    @Required
    public void setAccount_apikey(String account_apikey) {
        this.account_apikey = account_apikey;
    }

}
