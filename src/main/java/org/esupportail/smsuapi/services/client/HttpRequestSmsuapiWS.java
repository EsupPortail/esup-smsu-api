package org.esupportail.smsuapi.services.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.utils.HttpUtils;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Required;

public class HttpRequestSmsuapiWS {

    private final Logger logger = new LoggerImpl(getClass());

    private String username;
    private String password;
    private String url;

	public void sendSms(int id, String recipient, String message) {
		sendSms(id, recipient, message, null);
	}
	public void sendSms(int id, String recipient, String message, String account) {
		sendSms(id, recipient, message, account, null);
	}
	public void sendSms(int id, String recipient, String message, String account, Integer senderId) {
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new NameValuePair("phoneNumber", recipient));
		params.add(new NameValuePair("message", message));
		params.add(new NameValuePair("id", ""+id));
		if (account != null) params.add(new NameValuePair("account", account));
		if (senderId != null) params.add(new NameValuePair("senderId", ""+senderId));
		request("SendSms", params);
	}

	public void mayCreateAccountCheckQuotaOk(String account, int nbDest) {
		NameValuePair[] params = {
			new NameValuePair("account", account),
			new NameValuePair("nbDest", ""+nbDest),
		};
		request("MayCreateAccountCheckQuotaOk", params);
	}

	public List<String> messageStatus(LinkedList<NameValuePair> params) {
		return convertToStringList(request("MessageStatus", params));
	}

	public TrackInfos getMessageStatus(Integer msgId) {
		NameValuePair[] params = {
				new NameValuePair("id", ""+msgId),
		};
		try {
			return (new ObjectMapper()).readValue(request("MessageInfos", params), TrackInfos.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String testConnexion() {
		NameValuePair[] params = {};
		JsonNode json = request("TestConnexion", params);
		return json.getTextValue();		
	}

	public Set<String> getListPhoneNumbersInBlackList() {
		NameValuePair[] params = {};
		JsonNode json = request("ListPhoneNumbersInBlackList", params);
		return convertToStringSet(json);	
	}

	public boolean isPhoneNumberInBlackList(String phoneNumber) {
		NameValuePair[] params = {
				new NameValuePair("phoneNumber", phoneNumber),
		};
		JsonNode json = request("IsBlacklisted", params);
		return json.getBooleanValue();
	}
    
	
	private List<String> convertToStringList(JsonNode json) {
		List<String> l = new LinkedList<String>();
		Iterator<JsonNode> it = json.getElements();		
		while (it.hasNext())
			l.add(it.next().getTextValue());
		return l;
	}
	
	private Set<String> convertToStringSet(JsonNode json) {
		HashSet<String> l = new HashSet<String>();
		Iterator<JsonNode> it = json.getElements();		
		while (it.hasNext())
			l.add(it.next().getTextValue());
		return l;
	}


    private JsonNode request(String action, NameValuePair[] params) {
	String cooked_url = HttpUtils.cook_url(url, "action", action, params);
	return request(cooked_url);
    }

    private JsonNode request(String action, Iterable<NameValuePair> params) {
	String cooked_url = HttpUtils.cook_url(url, "action", action, params);
	return request(cooked_url);
    }

    private JsonNode request(String cooked_url) {
	logger.warn("asking " + cooked_url);
	String json;
	try {
		json = HttpUtils.requestGET(HttpUtils.basicAuth(username, password), cooked_url);
	} catch (IOException e) {
		throw new SmsuapiWSException(e);
	}
	logger.warn("response " + json);
	try {
		return new ObjectMapper().readTree(json);
	} catch (IOException e) {
		throw new SmsuapiWSException(e);
	}
    }
	
	@Required
	public void setUsername(String username) {
	this.username = username;
    }
	@Required
    public void setPassword(String password) {
	this.password = password;
    }
	@Required
    public void setUrl(String url) {
	this.url = url;
    }

}
