package org.esupportail.smsuapi.services.sms.impl.smsenvoi;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

public class RequestSmsenvoi {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	private String account_email;
	private String account_apikey;
	
	public static JSONObject json_decode(String s) {
		Object o = JSONValue.parse(s);
		try {
			return (JSONObject) o;
		} catch (NullPointerException e) {
		} catch (ClassCastException e) {
		}
		return null;
	}

	public static String urlencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("urlencode failed on '" + s + "'");
		}
	}
	
	public JSONObject request(String url, Map<String, String> params) throws IOException {
		params.put("email", account_email);
		params.put("apikey", account_apikey);
		String response = requestGET(cook_url(url, params));
		logger.debug("Smsenvoi raw response: " + response);
		return response == null ? null : json_decode(response);
	}

	private static String cook_url(String url, Map<String, String> params) {
		String s = null;
		for (Entry<String, String> e : params.entrySet()) {
			s = (s == null ? "?" : s + "&") + e.getKey() + "=" + urlencode(e.getValue());
		}
		return url + s;
	}

	private String requestGET(String request) throws IOException {
		logger.debug("requesting url " + request);

		GetMethod method = new GetMethod(request);       

		try {
		    // Execute the method.
		    int statusCode = new HttpClient().executeMethod(method);

		    if (statusCode != HttpStatus.SC_OK) {
			throw new IOException("GET failed with status " + method.getStatusLine());
		    }

		    // Read the response body.
		    String resp = method.getResponseBodyAsString();

		    logger.debug(resp);
		    return resp;
		    
		} catch (HttpException e) {
		    throw new IOException("Fatal protocol violation: " + e.getMessage());
		} finally {
		    // Release the connection.
		    method.releaseConnection();
		}
	}

	public void setAccount_email(String account_email) {
		this.account_email = account_email;
	}
	public void setAccount_apikey(String account_apikey) {
		this.account_apikey = account_apikey;
	}
}
