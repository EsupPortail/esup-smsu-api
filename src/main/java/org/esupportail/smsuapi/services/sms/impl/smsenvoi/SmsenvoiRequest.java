package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import java.util.ArrayList;
import java.util.List;

import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;

public class SmsenvoiRequest {

    public SmsenvoiRequest(String smsMessage, List<SMSBroker.Rcpt> smsRecipients, String senderLabel, String id) {
        message = smsMessage;
        sender = senderLabel;
        recipient = new ArrayList<>();
        for (SMSBroker.Rcpt smsRecipient : smsRecipients) {
            recipient.add(smsRecipient.recipient);
        }
        order_id = id;
    }

    public String message_type = "PRM";
    public String message;
    public List<String> recipient;    
    public String sender;
    public String order_id;
}
