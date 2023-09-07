package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsenvoiResponse {
    public String result;
    public String order_id;
    public Long total_sent;
}
