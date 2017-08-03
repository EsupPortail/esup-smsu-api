package org.esupportail.smsuapi.services.servlet;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Servlet used to access webservices through simple REST
 *
 */
public class RestServlet implements org.springframework.web.HttpRequestHandler {

	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired private RestServletActions restServletActions;

	public void setRestServletActions(RestServletActions restServletActions) {
		this.restServletActions = restServletActions;
	}
	
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Method m = getAction(RestServletActions.getString(req, "action"));
			executeMethodRaw(m, req, resp);
		} catch (Throwable e) {
			answerError(resp, e);
		}

	}

	private void executeMethodRaw(Method m, HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		if (!restServletActions.isAuthValid(req)) {
			answerBasicAuthNeeded(resp);
			return;
		}
		logger.info("invoking " + m.getName());
		Object val;
		try { 
			val = m.invoke(restServletActions, req);
		} catch (java.lang.reflect.InvocationTargetException e) {
			throw e.getCause();
		}
		writeJson(resp, val);
	}

	private void answerBasicAuthNeeded(HttpServletResponse resp) {
		String err = "HTTP authentication needed";
		logger.info(err);
		String s = "Basic realm=\"Smsuapi\"";
		resp.setHeader("WWW-Authenticate", s);
		resp.setStatus(401);
		writeJson(resp, err);
	}
	private void answerError(HttpServletResponse resp, Throwable e) {
		String exnName = e.getClass().getSimpleName();
		if (exnName.equals("UnknownMessageIdException") || exnName.equals("InvalidParameterException") || exnName.equals("InsufficientQuotaException")) {
			// known exceptions, no need to pollute logs with backtrace
			logger.error(""+e);
		} else {
			logger.error(e);
		}
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		Map<String, String> map = new HashMap<>();
		map.put("error", exnName);
		if (!StringUtils.isEmpty(e.getMessage())) map.put("message", e.getMessage());
		writeJson(resp, map);
	}

	private void writeJson(HttpServletResponse resp, Object val) {
		try {
			String jsonVal= new ObjectMapper().writeValueAsString(val);
			logger.debug("jsonVal: " + jsonVal);
			getJsonWriter(resp).write(jsonVal);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private PrintWriter getJsonWriter(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");         
		resp.setCharacterEncoding("UTF-8");        
		return resp.getWriter();
	}

	private Method getAction(String actionName) {
		Map<String, Method> actions = getActionsThroughReflection();

		Method m = actions.get(actionName);
		if (m == null) {
			String err = "Action " + actionName + " is unknown. Known actions: " + join(actions.keySet(), ", ");
			logger.error(err);
			throw new RuntimeException(err);
		}
		return m;
	}

	private Map<String, Method> getActionsThroughReflection() {
		Map<String, Method> actions = new HashMap<>();
		for (Method m : RestServletActions.class.getDeclaredMethods()) {
			String action = removePrefixOrNull(m.getName(), "wsAction");
			if (action != null)
				actions.put(action, m);
		}
		return actions;
	}

	private String removePrefixOrNull(String s, String prefix) {
		return s.startsWith(prefix) ? s.substring(prefix.length()) : null;
	}

	public static String join(Iterable<?> elements, CharSequence separator) {
		if (elements == null) return "";

		StringBuilder sb = null;

		for (Object s : elements) {
			if (sb == null)
				sb = new StringBuilder();
			else
				sb.append(separator);
			sb.append(s);			
		}
		return sb == null ? "" : sb.toString();
	}
}
