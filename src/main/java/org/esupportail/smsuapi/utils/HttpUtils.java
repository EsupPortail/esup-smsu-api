package org.esupportail.smsuapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

public class HttpUtils {

	private static final Logger logger = new LoggerImpl(HttpUtils.class);

	private static int defaultConnectTimeout = 10; // seconds
	
	public static class Pair {
		String a;
		String b;
		public Pair(String a, String b) {
			this.a = a;
			this.b = b;
		}		
	}
	
	public static HttpURLConnection basicAuth(HttpURLConnection uc, String username, String password) { 
        String userpass = username + ":" + password;
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
        uc.setRequestProperty ("Authorization", basicAuth);
        return uc;
	}

	public static HttpURLConnection openConnection(String request) throws HttpException {
		return openConnection(request, defaultConnectTimeout);
	}

	public static HttpURLConnection openConnection(String request, int timeout) throws HttpException {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(request).openConnection();
			conn.setConnectTimeout(timeout * 1000);
			return conn;
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

    public static String requestGET(HttpURLConnection conn) throws HttpException {
        //logger.debug("requesting url " + request);
        //conn.setUseCaches(false);
        return requestRaw(conn);
    }

    public static String requestPOST(HttpURLConnection conn, List<Pair> params) throws HttpException {
        //conn.setUseCaches(false);
     	conn.setDoOutput(true); // true indicates POST request

    	try {
        	// sends POST
			IOUtils.write(formatParams(params), conn.getOutputStream());
		} catch (IOException e) {
			throw new HttpException(e);
		}
    	return requestRaw(conn);
    }

    private static String requestRaw(HttpURLConnection conn) throws HttpException {
    	try {
			conn.connect();			
        	if (conn.getResponseCode() >= 400) {
            	logger.error(conn.getURL() + " error: " + conn.getResponseMessage());
            	throw new HttpException.WithStatusCode(conn);
            }
    	} catch (UnknownHostException e) {
    		throw new HttpException.Unreachable(e);
    	} catch (ConnectException e) {
    		throw new HttpException.Unreachable(e);
    	} catch (SSLException e) {
        	throw new HttpException.Unreachable(e);
    	} catch (SocketTimeoutException e) { // connect timeout or read timeout(?)
        	throw new HttpException.Unreachable(e);
        } catch (IOException e) {
        	throw new HttpException(e);
        }
	
        InputStream inputStream = null;
    	try {
            // Read the response body.
            inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream, "UTF-8");
            //logger.debug(resp);
            return resp;
        } catch (IOException e) {
        	throw new HttpException(e);
        } finally {
        	if (inputStream != null) {
                // Release the connection.
				try { inputStream.close(); } catch (IOException e) {}
        	}
        }
    }

    public static String cook_url(String url, String name1, String val1, List<Pair> params) {
        return url + "?" + urlencode(name1) + "=" + urlencode(val1) + "&" + formatParams(params);
    }

    public static String cook_url(String url, String name1, String val1, Pair[] params) {
        return url + "?" + urlencode(name1) + "=" + urlencode(val1) + "&" + formatParams(params);
    }

	private static String formatParams(Pair[] params) {
		StringBuffer requestParams = null;
		for (Pair param : params) {
		    if (requestParams == null) {
		    	requestParams = new StringBuffer();
		    } else {
		    	requestParams.append("&");
		    }
		    requestParams.append(urlencode(param.a)).append("=").append(urlencode(param.b));
		}
		return requestParams == null ? null : requestParams.toString();
	}

	private static String formatParams(List<Pair> params) {
		StringBuffer requestParams = null;
		for (Pair param : params) {
		    if (requestParams == null) {
		    	requestParams = new StringBuffer();
		    } else {
		    	requestParams.append("&");
		    }
		    requestParams.append(urlencode(param.a)).append("=").append(urlencode(param.b));
		}
		return requestParams == null ? null : requestParams.toString();
	}

    public static String urlencode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("urlencode failed on '" + s + "'");
        }
    }
	
	public static JsonNode json_decode(String s) {
		try {
			return (new ObjectMapper()).readTree(s);
		} catch (IOException e) {
			return null;
		}
	}
    
}
