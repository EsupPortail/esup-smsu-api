package org.esupportail.smsuapi.services.sms.impl.proxy;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.services.remote.SmsuapiStatus;
import org.esupportail.ws.remote.beans.MsgIdAndPhone;

public class AckStatusProxy {
		
	/**
	 * Proxy connector used to ack status
	 */
	private SmsuapiStatus smsuapiStatus;
	
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
			List<String> statuss = smsuapiStatus.getStatus(toMsgIdAndPhoneList(smss));
			Iterator<Sms> it_in = smss.iterator();
			Iterator<String> status_it = statuss.iterator();		
			while (it_in.hasNext() && status_it.hasNext()) {
				updateStatus(it_in.next(), status_it.next());
			}
		} catch (UnknownIdentifierApplicationException e) {
			logger.error("AckStatusProxy: the proxy does not know us", e);
		}
		
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

	private List<MsgIdAndPhone> toMsgIdAndPhoneList(List<Sms> smss) {
		List<MsgIdAndPhone> l = new LinkedList<MsgIdAndPhone>();
		for (Sms sms_ : smss) l.add(new MsgIdAndPhone(sms_.getId(), sms_.getPhone()));
		return l;
	}
	
	
	/*******************
	 * Mutator.
	 *******************/

	/**
	 * Standard setter used by Spring.
	 */
	public void setSmsuapiStatus(final SmsuapiStatus smsuapiStatus) {
		this.smsuapiStatus = smsuapiStatus;
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
