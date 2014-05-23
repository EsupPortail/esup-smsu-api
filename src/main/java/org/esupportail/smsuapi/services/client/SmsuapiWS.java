package org.esupportail.smsuapi.services.client;

import java.io.IOException;

import org.esupportail.smsuapi.utils.HttpException;

public class SmsuapiWS {
	
	static public class UnreachableException extends HttpException.Unreachable {
		private static final long serialVersionUID = 1L;
		public UnreachableException(Exception e) { super(e); }
		public String toString() {
			return "Unreachable: " + getCause();
		}
	}
	
	static public class AuthenticationFailedException extends HttpException {
		private static final long serialVersionUID = 1L;
		public AuthenticationFailedException(HttpException.WithStatusCode e) { super(e);	}
	}
	
	static public class BadJsonException extends HttpException {
		private static final long serialVersionUID = 1L;		
		private String badJson;		
		public String getBadJson() { return badJson; }
		public BadJsonException(String badJson, IOException e) { super(e); this.badJson = badJson; }
		public String toString() {
			return "BadJson: " + badJson;
		}
	}
	
}
