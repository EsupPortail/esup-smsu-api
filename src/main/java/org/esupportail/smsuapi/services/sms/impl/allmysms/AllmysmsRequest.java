package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.util.Collections;
import java.util.List;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class AllmysmsRequest {

    public AllmysmsRequest(String smsMessage, String smsRecipient, String senderLabel) {
        DATA = new DATA();
        DATA.MESSAGE = smsMessage;
        SMS sms = new SMS();
        sms.MOBILEPHONE = smsRecipient;
        DATA.SMS = Collections.singletonList(sms);
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
        return mapper.writeValueAsString(this);
    }

}
