package org.esupportail.smsuapi.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
public class HttpException extends Exception {
	
	static public class Unreachable extends HttpException {
		public Unreachable(Exception e) {
			super(e);
		}
	}
	
	static public class WithStatusCode extends HttpException {
		private int statusCode = -1;
		private String messageLine;
		private String errorMessage;
		private URL url;
		
		public WithStatusCode(HttpURLConnection conn) {
			url = conn.getURL();
			try {
				statusCode = conn.getResponseCode();
				messageLine = conn.getResponseMessage();
				errorMessage = IOUtils.toString(conn.getErrorStream(), "UTF-8");
			} catch (IOException e) {
				// should never happen!
				throw new RuntimeException("internal error: HttpException could not be created", e);
			}
		}	

		public int getStatusCode() {
			return statusCode;
		}

		public String getMessageLine() {
			return messageLine;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public URL getUrl() {
			return url;
		}		
		
		public String toString() {
			return "HttpException: " + statusCode + " " + messageLine + "\n" + errorMessage;
		}
	}

	public HttpException() {
		super();
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}	
}
