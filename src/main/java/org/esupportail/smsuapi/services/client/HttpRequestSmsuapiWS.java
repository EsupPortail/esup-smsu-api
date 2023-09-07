package org.esupportail.smsuapi.services.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.exceptions.AlreadySentException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.smsuapi.utils.HttpUtils;
import org.esupportail.smsuapi.utils.HttpUtils.Pair;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.springframework.beans.factory.annotation.Required;

public class HttpRequestSmsuapiWS {

    private final Logger logger = Logger.getLogger(getClass());

    private String username;
    private String password;
    private String url;

	public HttpRequestSmsuapiWS() {}
      
	public HttpRequestSmsuapiWS(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public Integer sendSms(Integer id, String recipient, String message) throws HttpException, InsufficientQuotaException {
		return sendSms(id, recipient, message, null);
	}
	public Integer sendSms(Integer id, String recipient, String message, String account) throws HttpException, InsufficientQuotaException {
		return sendSms(id, recipient, message, account, null);
	}
	public Integer sendSms(Integer id, String recipient, String message, String account, Integer senderId) throws HttpException, InsufficientQuotaException {
		return sendSms(id, singletonListOrEmpty(recipient), message, account, senderId);
	}
	public Integer sendSms(Integer id, List<String> recipients, String message, String account, Integer senderId) throws HttpException, InsufficientQuotaException {
		List<Pair> params = new LinkedList<>();
		for (String recipient : recipients) {
		    params.add(new Pair("phoneNumber", recipient));
		}
		params.add(new Pair("message", message));
		if (id != null) {
			params.add(new Pair("id", ""+id));
		}
		if (account != null) params.add(new Pair("account", account));
		if (senderId != null) params.add(new Pair("senderId", ""+senderId));
		JsonNode json = requestPOST("SendSms", params);
		return convertToInteger(json.path("id"));
	}
	
	public void mayCreateAccountCheckQuotaOk(String account, int nbDest) throws HttpException, InsufficientQuotaException {
		Pair[] params = {
			new Pair("account", account),
			new Pair("nbDest", ""+nbDest),
		};
		request("MayCreateAccountCheckQuotaOk", params);
	}

	public List<String> messageStatus(List<Pair> params) throws HttpException, UnknownMessageIdException {
		return convertToStringList(request("MessageStatus", params));
	}

	public TrackInfos messageInfos(Integer msgId) throws HttpException, UnknownMessageIdException {
		Pair[] params = {
				new Pair("id", ""+msgId),
		};
		try {
			JsonNode json = request("MessageInfos", params);
			return (new ObjectMapper()).treeToValue(json, TrackInfos.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    @Deprecated
	public String testConnexion() throws HttpException {
		Pair[] params = {};
		JsonNode json = request("TestConnexion", params);
		return json.textValue();		
	}

	public Set<String> getListPhoneNumbersInBlackList() throws HttpException {
		Pair[] params = {};
		JsonNode json = request("ListPhoneNumbersInBlackList", params);
		return convertToStringSet(json);	
	}

	public boolean isPhoneNumberInBlackList(String phoneNumber) throws HttpException {
		Pair[] params = {
				new Pair("phoneNumber", phoneNumber),
		};
		JsonNode json = request("IsBlacklisted", params);
		return json.booleanValue();
	}
    
	private Integer convertToInteger(JsonNode json) {
		return json.isInt() ? json.intValue() : null;
	}
	
	private List<String> convertToStringList(JsonNode json) {
		List<String> l = new LinkedList<>();
		Iterator<JsonNode> it = json.elements();		
		while (it.hasNext())
			l.add(it.next().textValue());
		return l;
	}
	
	private Set<String> convertToStringSet(JsonNode json) {
		HashSet<String> l = new HashSet<>();
		Iterator<JsonNode> it = json.elements();		
		while (it.hasNext())
			l.add(it.next().textValue());
		return l;
	}

    private JsonNode requestPOST(String action, List<Pair> params) throws HttpException {
        String cooked_url = HttpUtils.cook_url(url, "action", action);
        String json;
        try {
            json = HttpUtils.requestPOST(HttpUtils.basicAuth(HttpUtils.openConnection(cooked_url), username, password), params);
        } catch (HttpException.WithStatusCode e) {
            throw remapException(e);
        }
        return parseResponse(json);
    }

    private JsonNode request(String action, List<Pair> params) throws HttpException {
	String cooked_url = HttpUtils.cook_url(url, "action", action, params);
	return request(cooked_url);
    }

    private JsonNode request(String action, Pair[] params) throws HttpException {
	String cooked_url = HttpUtils.cook_url(url, "action", action, params);
	return request(cooked_url);
    }

    private JsonNode request(String cooked_url) throws HttpException {
		logger.warn("asking " + cooked_url);
		String json;
		try {
			json = HttpUtils.requestGET(HttpUtils.basicAuth(HttpUtils.openConnection(cooked_url), username, password));
		} catch (HttpException.WithStatusCode e) {
			throw remapException(e);
		}
		return parseResponse(json);
    }

    private JsonNode parseResponse(String json) throws HttpException {
        logger.warn("response " + json);
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            throw new SmsuapiWS.BadJsonException(json, e);
        }
    }

    private HttpException remapException(HttpException.WithStatusCode e) {
        if (e.getStatusCode() == 404)
            return new SmsuapiWS.UnreachableException(e);
        else if (e.getStatusCode() == 401)
            return new SmsuapiWS.AuthenticationFailedException(e);
        else if (e.getStatusCode() == 400) {
            JsonNode jsonErr = HttpUtils.json_decode(e.getErrorMessage());
            if (jsonErr != null) {
                String error = jsonErr.path("error").textValue();
                if (error == null) {
                } else if (error.equals("UnknownMessageIdException")) {
                    Unchecked.throw_(new UnknownMessageIdException());
                } else if (error.equals("InvalidParameterException")) {
                    throw new InvalidParameterException(jsonErr.path("message").textValue());
                } else if (error.equals("AlreadySentException")) {
                    throw new AlreadySentException(jsonErr.path("message").textValue());
                } else if (error.equals("InsufficientQuotaException")) {
                    Unchecked.throw_(new InsufficientQuotaException(jsonErr.path("message").textValue()));
                }
            }
        }
        // we could not find a more useful exception
        return e;
    }

	private <A> LinkedList<A> singletonListOrEmpty(A e) {
		final LinkedList<A> l = new LinkedList<>();
		if (e != null) l.add(e);
		return l;
	}	
	
	static class Unchecked{
		/**
		* Throw any kind of exception without needing it to be checked
		* @param e any instance of a Exception
		*/
		public static void throw_(Exception e) {
			/**
			 * Abuse type erasure by making the compiler think we are throwing RuntimeException,
			 * which is unchecked, but then inserting any exception in there.
			 */
			Unchecked.<RuntimeException>throwAnyImpl(e);
		}
		
		@SuppressWarnings("unchecked")
		private static<T extends Exception> void throwAnyImpl(Exception e) throws T {
			throw (T) e;
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
