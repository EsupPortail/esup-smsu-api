package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;

public class AllmysmsRequest {

    public AllmysmsRequest(String smsMessage, List<SMSBroker.Rcpt> smsRecipients, String senderLabel) {
        DATA = new DATA();
        DATA.MESSAGE = smsMessage;
        ArrayList<SMS> smss = new ArrayList<>();
        for (SMSBroker.Rcpt smsRecipient : smsRecipients) {
        SMS sms = new SMS();
            sms.MOBILEPHONE = smsRecipient.recipient;
            smss.add(sms);
        }
        DATA.SMS = smss;
        DATA.TPOA = senderLabel;
    }

    public class SMS {
        public String MOBILEPHONE;
        public String PARAM_1;
        public String PARAM_2;
    }

    public class DATA {
        public String CAMPAIGN_NAME;
        public String MESSAGE;
        public String TPOA;
        public String DYNAMIC;
        public String DATE;
        public List<SMS> SMS;
    }

    public DATA DATA;

    public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsString(this);
    }

}
