package org.esupportail.smsuapi.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

public class HttpUtils {

	private static final Logger logger = new LoggerImpl(HttpUtils.class);

    public static HttpClient basicAuth(String username, String password) { 
	HttpClient client = new HttpClient();
	client.getParams().setAuthenticationPreemptive(true);
	client.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
					 new UsernamePasswordCredentials(username, password));
	return client;
    }

    public static String requestGET(HttpClient client, String request) throws IOException {
        //logger.debug("requesting url " + request);
        return requestRaw(client, new GetMethod(request));
    }

    public static String requestPOST(HttpClient client, String url, NameValuePair[] params) throws IOException {
	PostMethod post = new PostMethod(url);
        post.setRequestBody(params);
	return requestRaw(client, post);
    }

    private static String requestRaw(HttpClient client, HttpMethod method) throws IOException {
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_NOT_FOUND) {
            	logger.error(method.getURI() + " failed with status: " + method.getStatusLine());
            	throw new IOException("GET failed with status " + method.getStatusLine());
            }

            // Read the response body.
            String resp = method.getResponseBodyAsString();

            if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            	logger.error(method.getURI() + " bad request: " + resp);
            	throw new IOException(method.getURI() + " bad request: " + resp);
            }

            //logger.debug(resp);
            return resp;
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }

    private static String urlencode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("urlencode failed on '" + s + "'");
        }
    }

    public static String cook_url(String url, String name1, String val1, NameValuePair[] params) {
        String s = "?" + name1 + "=" + urlencode(val1);
        for (NameValuePair e : params) {
            s = s + "&" + e.getName() + "=" + urlencode(e.getValue());
        }
        return url + s;
    }

    public static String cook_url(String url, String name1, String val1, Iterable<NameValuePair> params) {
        String s = "?" + name1 + "=" + urlencode(val1);
        for (NameValuePair e : params) {
            s = s + "&" + e.getName() + "=" + urlencode(e.getValue());
        }
        return url + s;
    }

}
