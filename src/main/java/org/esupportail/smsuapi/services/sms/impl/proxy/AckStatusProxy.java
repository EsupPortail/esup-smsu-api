package org.esupportail.smsuapi.services.sms.impl.proxy;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.services.client.HttpRequestSmsuapiWS;

public class AckStatusProxy {
		
	private HttpRequestSmsuapiWS httpRequestSmsuapiWS;
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;
	
	/**
	 * use to simulate sending.
	 */
	private double nbHoursBeforeGivingUp;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	

	public void smsuapiStatus() {
		List<Sms> smss = daoService.getSms(SmsStatus.IN_PROGRESS);
		if (smss.isEmpty()) {
		    logger.debug("AckStatusProxy: nothing to do");		
		    return;
		}

		logger.info("AckStatusProxy: asking the proxy for the status of some sms");
		try {
			List<String> statuss = wsMessageStatus(smss);
			Iterator<Sms> it_in = smss.iterator();
			Iterator<String> status_it = statuss.iterator();		
			while (it_in.hasNext() && status_it.hasNext()) {
				updateStatus(it_in.next(), status_it.next());
			}
		} catch (IOException e) {
			logger.error("AckStatusProxy: the proxy does not know us", e);
		}
		
	}

	private List<String> wsMessageStatus(List<Sms> smss) throws IOException {
		return httpRequestSmsuapiWS.messageStatus(convertSmsStatusParams(smss));
	}

	private LinkedList<NameValuePair> convertSmsStatusParams(List<Sms> smss) {
		LinkedList<NameValuePair> params = new LinkedList<NameValuePair>();
		for (Sms sms : smss) {
			params.add(new NameValuePair("id", ""+sms.getId()));
			params.add(new NameValuePair("phoneNumber", sms.getPhone()));
		}
		return params;
	}

	private void updateStatus(Sms sms, String status) {
		if (status == null) {
			logger.error("proxy smsuapi failed on " + sms.getId() + ":" + sms.getPhone() + ", hopefully because the sms has not been sent to the proxy yet");
		} else {
			logger.info("proxy smsuapi returned " + status + " for " + sms.getId() + ":" + sms.getPhone());
		}
		if (status == null || status.equals("IN_PROGRESS")) {
			double h = dateDifferenceInHours(new Date(), sms.getDate());
			if (h < nbHoursBeforeGivingUp)
				return; // ignore for the moment
			else {
				logger.error(String.format("After %.1f hours proxy smsuapi still return %s for " + sms.getId() + ":" + sms.getPhone() + ", giving up (marking as ERROR)", h, status));
				status = "ERROR";
			}
		}
		SmsStatus s = SmsStatus.valueOf(status);
		sms.setStateAsEnum(s);
		daoService.updateSms(sms);
	}


	public static double dateDifferenceInHours(Date a, Date b) {
		double deltaInMilliSeconds = a.getTime() - b.getTime();
		return deltaInMilliSeconds / 1000 / 60 / 60;
	}
	
	
	/*******************
	 * Mutator.
	 *******************/

	/**
	 * Standard setter used by Spring.
	 */
	public void setHttpRequestSmsuapiWS(HttpRequestSmsuapiWS httpRequestSmsuapiWS) {
		this.httpRequestSmsuapiWS = httpRequestSmsuapiWS;
	}

	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	/**
	 * Standard setter used by spring.
	 * @param nbHoursBeforeGivingUp
	 */
	public void setNbHoursBeforeGivingUp(final double nbHoursBeforeGivingUp) {
		this.nbHoursBeforeGivingUp = nbHoursBeforeGivingUp;
	}	
	

}
