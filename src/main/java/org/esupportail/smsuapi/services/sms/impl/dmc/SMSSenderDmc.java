package org.esupportail.smsuapi.services.sms.impl.dmc;

import java.util.List;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * SFR DMC SMS sender.
 */
public class SMSSenderDmc implements ISMSSender {

	protected final Logger logger = Logger.getLogger(getClass());
	
	protected boolean simulateMessageSending;
	
	protected DmcAuthenticate authenticate;
	
	protected String dmcWsUrl;
	
	protected String cookieJsessionId = null;

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

			// only send the message if required
			if (!simulateMessageSending) {
					
				HttpHeaders requestHeaders = new HttpHeaders();	
				DmcMessageUnitaire messageUnitaire = new DmcMessageUnitaire(smsMessage, smsRecipient);
				
				// DMC is waiting for a mix with application/x-www-form-urlencoded and json data ... 
				// so restTemplate uses here only FormHttpMessageConverter - the object->json is made directly with ObjectMapper
				
				MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
				params.add("messageUnitaire", messageUnitaire.toJson());
				
				
				/* DOESN'T WORK !?
				 * => TODO - WHEN THE DOC WILL BE AVAILABLE (404 TODAY - 25/11/2015) !! https://www.dmc.sfr-sh.fr/ApiWorkshop/
				 *
				if(cookieJsessionId==null) {
					params.add("authenticate", authenticate.toJson());
				} else {
					requestHeaders.add("Cookie", cookieJsessionId);
				}
				*/
				params.add("authenticate", authenticate.toJson());
				if(cookieJsessionId!=null) {
					requestHeaders.add("Cookie", cookieJsessionId);
				} 
				
				HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, requestHeaders);
				
				// restemplate will use here jacksonHttpMessageConverter for json->object
				ResponseEntity<DmcResponse> dmcRespEntity = restTemplate.exchange(dmcWsUrl, HttpMethod.POST, entity, DmcResponse.class);
				DmcResponse dmcResp = dmcRespEntity.getBody();
	
				if(dmcResp.getSuccess()) {
					List<String> cookies = dmcRespEntity.getHeaders().get("Set-Cookie");
					if(cookies!=null) {
						cookieJsessionId = cookies.get(0);
					}
				}
				
				logger.info(dmcResp.toString());	
				
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


	@Required
	public void setSimulateMessageSending(final boolean simulateMessageSending) {
		this.simulateMessageSending = simulateMessageSending;
	}
	
	@Required
	public void setAuthenticate(DmcAuthenticate authenticate) {
		this.authenticate = authenticate;
	}

	@Required
	public void setDmcWsUrl(String dmcWsUrl) {
		this.dmcWsUrl = dmcWsUrl;
	}

	@Required
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	
	
}
