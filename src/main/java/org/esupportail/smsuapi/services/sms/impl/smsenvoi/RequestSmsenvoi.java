package org.esupportail.smsuapi.services.sms.impl.smsenvoi;


import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Map.Entry;



import org.codehaus.jackson.JsonNode;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.smsuapi.utils.HttpUtils;

public class RequestSmsenvoi {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	private String account_email;
	private String account_apikey;
	
	public JsonNode request(String url, Map<String, String> params) throws HttpException {
		params.put("email", account_email);
		params.put("apikey", account_apikey);
		String response = requestGET(cook_url(url, params));
		logger.debug("Smsenvoi raw response: " + response);
		return response == null ? null : HttpUtils.json_decode(response);
	}

	private static String cook_url(String url, Map<String, String> params) {
		String s = null;
		for (Entry<String, String> e : params.entrySet()) {
			s = (s == null ? "?" : s + "&") + e.getKey() + "=" + HttpUtils.urlencode(e.getValue());
		}
		return url + s;
	}

	private String requestGET(String request) throws HttpException {
		logger.debug("requesting url " + request);
		
		HttpURLConnection conn = HttpUtils.openConnection(request);
		return HttpUtils.requestGET(conn);
	}

	public void setAccount_email(String account_email) {
		this.account_email = account_email;
	}
	public void setAccount_apikey(String account_apikey) {
		this.account_apikey = account_apikey;
	}
}
