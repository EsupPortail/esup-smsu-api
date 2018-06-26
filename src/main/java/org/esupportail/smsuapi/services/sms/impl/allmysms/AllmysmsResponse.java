package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.util.List;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllmysmsResponse {
    public static class SmsId {
        public String phoneNumber;
        public String smsId;
    }

    public Integer status;
    public String statusText;
    public String invalidNumbers;
    public String campaignId;
    public String credits;
    public String creditsUsed;
    public String nbCredits; // obsolete? replaced with nbCredits?
    public String nbContacts;
    public String nbSms;
    public List<SmsId> smsIds;

    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            Logger.getLogger(getClass()).warn("AllmysmsResponse.toString failed", e);
            return null;
        }
    }

    static public AllmysmsResponse fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, AllmysmsResponse.class);
        } catch (IOException e) {
            Logger.getLogger(AllmysmsResponse.class).error("AllmysmsResponse.fromJson failed", e);
            return null;
        }
    }

}
