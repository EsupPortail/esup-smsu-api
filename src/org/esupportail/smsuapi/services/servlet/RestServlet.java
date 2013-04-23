package org.esupportail.smsuapi.services.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.smsuapi.business.context.ApplicationContextUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;

/**
 * Servlet used to access webservices through simple REST
 *
 */
public class RestServlet extends HttpServlet {

	private final Logger logger = new LoggerImpl(getClass());
	
	private RestServletActions restServletActions = null;

	private RestServletActions getRestServletActions() {
		if (restServletActions == null) {
			restServletActions = (RestServletActions) BeanUtils.getBean("restServletActions");
		}
		return restServletActions;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}

		
	private void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Method m = getAction(RestServletActions.getString(req, "action"));
			executeMethodWithDb(m, req, resp);
		} catch (Throwable e) {
			answerError(resp, e);
		}

	}

	private void executeMethodWithDb(Method m, HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		ServletRequestAttributes previousRequestAttributes = null;
		try {
			previousRequestAttributes = ContextUtils.bindRequestAndContext(req, getServletContext());
			BeanUtils.initBeanFactory(getServletContext());
			DatabaseUtils.open();
			DatabaseUtils.begin();
			ApplicationContextUtils.initApplicationContext();
			executeMethodRaw(m, req, resp);   		
			DatabaseUtils.commit();
		} finally {
			DatabaseUtils.close();
			ContextUtils.unbindRequest(previousRequestAttributes);
		}
	}


	private void executeMethodRaw(Method m, HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		if (!getRestServletActions().isAuthValid()) {
			answerBasicAuthNeeded(resp);
			return;
		}
		logger.info("invoking " + m.getName());
		Object val;
		try { 
			val = m.invoke(getRestServletActions(), req);
		} catch (java.lang.reflect.InvocationTargetException e) {
			throw e.getCause();
		}
		writeJson(resp, val);
	}

	private void answerBasicAuthNeeded(HttpServletResponse resp) {
		String err = "HTTP authentication needed";
		logger.error(err);
		String s = "Basic realm=\"Smsuapi\"";
		resp.setHeader("WWW-Authenticate", s);
		resp.setStatus(401);
		writeJson(resp, err);
	}
	private void answerError(HttpServletResponse resp, Throwable e) {
		logger.error(e);
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		writeJson(resp, "" + e);
	}

	private void writeJson(HttpServletResponse resp, Object val) {
		try {
			String jsonVal= new Gson().toJson(val);
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
		Map<String, Method> actions = new HashMap<String, Method>();
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