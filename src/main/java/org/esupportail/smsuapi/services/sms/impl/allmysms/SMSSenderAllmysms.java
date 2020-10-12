package org.esupportail.smsuapi.services.sms.impl.allmysms;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.esupportail.smsuapi.services.sms.impl.smsenvoi.SMSSenderSmsenvoi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.esupportail.smsuapi.utils.HttpUtils;
import static org.esupportail.smsuapi.utils.Various.firstNonNull;

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

    @Inject
    private DaoService daoService;
    protected RestTemplate restTemplate;

    public synchronized void sendMessage(final SMSBroker sms, String forceLogin, String forcePassword) {
        try {
            if (logger.isDebugEnabled()) {
                final String messageISOLatin = new String(sms.message.getBytes(), "ISO-8859-1");
                logger.debug("sending encoded message : " + messageISOLatin);
            }

            AllmysmsRequest req = new AllmysmsRequest(sms.message, sms.rcpts, computeSenderlabel(sms));

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("login", firstNonNull(forceLogin, account_login));
            params.add("apiKey", firstNonNull(forcePassword, account_apikey));
            params.add("smsData", req.toJson());

            // restTemplate will use here jacksonHttpMessageConverter for response json->object
            ResponseEntity<AllmysmsResponse> respEntity = restTemplate.exchange(
                    simulateMessageSending ? simulate_wsUrl : wsUrl, HttpMethod.POST, new HttpEntity<>(params, null),
                    AllmysmsResponse.class);
            AllmysmsResponse resp = respEntity.getBody();
            logger.info("allmysms response " + resp.toString());
            save_message_id(sms, resp);
        } catch (Throwable t) {
            logger.error("An error occurs sending SMS " + sms.message, t);
            for (SMSBroker.Rcpt s : sms.rcpts) {
                logger.error(" to " + s.id + " - recipient : " + s.recipient + " - message : " + sms.message);
            }
        }

    }

    private void save_message_id(SMSBroker sms, AllmysmsResponse response) {
      Set<String> invalidNumbers = new HashSet<>(Arrays.asList(
            response.invalidNumbers != null ? response.invalidNumbers.split("\\|") : new String[] {}
      ));
        
      boolean error = response.status == null || response.status != 100;
      if (error) logger.error("allmysend send failed: " + response);

      int i = 0;
      for (SMSBroker.Rcpt s : sms.rcpts) {
        Sms smsDB = daoService.getSms(s.id);

        if (error) {
            smsDB.setStateAsEnum(SmsStatus.ERROR);
        } else if (invalidNumbers.contains(smsDB.getPhone())) {
            smsDB.setStateAsEnum(SmsStatus.ERROR_POST_BL);
        } else if (response.smsIds != null) {
            logger.debug("getting response for " + smsDB.getPhone() + ", it should be #" + i + " in smsIds");
            AllmysmsResponse.SmsId respIds = response.smsIds.get(i++);
            smsDB.setBrokerId(respIds.smsId);
        }

        daoService.updateSms(smsDB);
    }
    }

    private String computeSenderlabel(SMSBroker sms) {
        return SMSSenderSmsenvoi.computeSenderlabel(logger, from, sms.accountLabel);
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
