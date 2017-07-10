package org.esupportail.smsuapi.services.sms.impl.smsenvoi;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.smsuapi.utils.HttpUtils;


/**
 * Smsenvoi implementation of the SMS sender.
 * @author prigaux
 *
 */
public class SMSSenderSmsenvoiImpl implements ISMSSender {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * use to simulate sending.
	 */
	private boolean simulateMessageSending;
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;

	private RequestSmsenvoi requestSmsenvoi;

	private String sendsms_url;

	private JsonNode from;

	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.services.sms.ISMSSender
	 * #sendMessage(org.esupportail.smsuapi.domain.beans.sms.SMSMessage)
	 */
	public void sendMessage(final SMSBroker sms) {
		
		final int smsId = sms.getId();
		final String smsRecipient = sms.getRecipient();
		final String smsMessage = sms.getMessage();
		
		logger.info("Send message to smsenvoi with parameter : ");
		logger.info("   - message id : " + smsId);
		logger.info("   - message recipient : " + smsRecipient);
		logger.info("   - message : " + smsMessage);
		
		try {
			
			// only send the message if required
			if (!simulateMessageSending) {
				save_message_id(sms, realSendMessage(sms));

				logger.info("message with : " + 
						  " - id : " + smsId + "successfully sent");
			} else {
				logger.warn("Message with id : " + smsId + " not sent because simlation mode is enable");
			}

			
		} catch (Throwable t) {
			logger.error("An error occurs sending SMS : " + 
				     " - id : " + smsId + 
				     " - recipient : " + smsRecipient + 
				     " - message : " + smsMessage, t);
		}		
	}

	private void save_message_id(SMSBroker sms, JsonNode response) {
		Long message_id = get_message_id(response);

		Sms smsDB = daoService.getSms(sms.getId());
		if (message_id == null) 
		    smsDB.setStateAsEnum(SmsStatus.ERROR);
		else
		    smsDB.setBrokerId(message_id.toString());
		daoService.updateSms(smsDB);
	}

	private Long get_message_id(JsonNode resp) {
		try {
			if (resp.path("success").getLongValue() == 1)
				return resp.path("message_id").getLongValue();
		} catch (NullPointerException | ClassCastException e) {
		}
		return null;
	}

	private String computeSenderlabel(SMSBroker sms) {
		return computeSenderlabel(logger, from, sms.getAccountLabel());
	}
	public static String computeSenderlabel(Logger logger, JsonNode from, String accountLabel) {
		String label = from.path(accountLabel).getTextValue();
		if (label == null) {
			label = from.path("").getTextValue();
			if (label == null)
				logger.info("no default senderlabel (cf sms.connector.smsenvoi.from.mapJSON), no sender label will be used");
			else
				logger.debug("no senderlabel for " + accountLabel + " in " + from + ". Defaulting to " + label);
		}
		logger.debug("senderlabel: " + label);
		return label == null ? null : (String) label;
	}

	private JsonNode realSendMessage(SMSBroker sms) throws HttpException {
		String senderlabel = computeSenderlabel(sms);
		Map<String, String> p = new HashMap<>();
		p.put("message[type]", "sms");
		p.put("message[subtype]", "PREMIUM");
		p.put("message[recipients]", sms.getRecipient());
		p.put("message[content]", sms.getMessage());
		if (!StringUtils.isEmpty(senderlabel)) p.put("message[senderlabel]", senderlabel);

		return requestSmsenvoi.request(sendsms_url, p);
	}
	
	/*******************
	 * Mutator.
	 *******************/
	
	/**
	 * Standard setter used by spring.
	 * @param simulateMessageSending
	 */
	public void setSimulateMessageSending(final boolean simulateMessageSending) {
		this.simulateMessageSending = simulateMessageSending;
	}

	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	public void setRequestSmsenvoi(final RequestSmsenvoi requestSmsenvoi) {
		this.requestSmsenvoi = requestSmsenvoi;
	}

	public void setSendsms_url(String sendsms_url) {
		this.sendsms_url = sendsms_url;
	}

	public void setFrom_mapJSON(String from_mapJSON) {
		this.from = HttpUtils.json_decode(from_mapJSON);
		if (this.from == null)
			logger.error("invalid from_mapJSON: " + from_mapJSON);
	}
}
