package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.codehaus.jackson.JsonNode;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.services.sms.impl.proxy.AckStatusProxy;
import org.esupportail.smsuapi.utils.HttpException;

public class AckStatusSmsenvoi {
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;

	private RequestSmsenvoi requestSmsenvoi;

	private String checkdelivery_url;
	
	/**
	 * use to simulate sending.
	 */
	private double nbHoursBeforeGivingUp;

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	

	public void smsuapiStatus() {
		List<Sms> smss = daoService.getSms(SmsStatus.IN_PROGRESS);
		if (smss.isEmpty()) {
		    logger.debug("AckStatusSmsenvoi: nothing to do");		
		    return;
		}

		logger.info("AckStatusSmsenvoi: asking smsenvoi for the status of some sms");
		for (Sms sms : smss) {
			String message_id = sms.getBrokerId();
			SmsStatus status = null;
			if (message_id != null) {
				try {
					status = getStatus(message_id);
					logger.info("smsenvoi smsuapi returned " + status + " for " + sms.getId() + ":" + sms.getBrokerId() + ":" + sms.getPhone());
				} catch (HttpException.Unreachable e) {
					logger.error(e);
				} catch (HttpException e) {
					logger.error("smsenvoi smsuapi failed on " + sms.getId() + ":" + sms.getBrokerId() + ":" + sms.getPhone() + ", hopefully because the sms has not been sent to smsenvoi yet", e);
				}
			}
			updateStatus(sms, status == null ? null : status.name());
		}
	}

	public String get_arcode(JsonNode resp) {
		if (resp.path("success").getLongValue() != 1) return null;
		try {			
			JsonNode firstElement = resp.path("listing").getElements().next();
			return firstElement.path("arcode").getTextValue();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private SmsStatus getStatus(String message_id) throws HttpException {
		Map<String, String> p = new HashMap<>();
		p.put("message_id", message_id);
		
		    JsonNode resp = requestSmsenvoi.request(checkdelivery_url, p);
		    String arcode = get_arcode(resp);
		    if (arcode == null)
			// it happens in case of invalid phone number. Other cases?
			return SmsStatus.ERROR;
		    else if ("0".equals(arcode))
			return SmsStatus.IN_PROGRESS;
		    else if ("1".equals(arcode))
			return SmsStatus.DELIVERED;
		    else if ("2".equals(arcode))
			// {"id":"xxx","recipient":"+336xxxx",
			//  "ar":"Non recu", "ardate":"2013-03-03","artime":"00:15:18","arcode":"2",
			//  "errorcode":"UNDELIV","errormsg":"Non livrable"}
			return SmsStatus.ERROR;
		    else
			return SmsStatus.ERROR;
	}

	private void updateStatus(Sms sms, String status) {
		if (status == null || status.equals("IN_PROGRESS")) {
			double h = AckStatusProxy.dateDifferenceInHours(new Date(), sms.getDate());
			if (h < nbHoursBeforeGivingUp)
				return; // ignore for the moment
			else if (sms.getBrokerId() == null) {
				logger.error("AckStatusSmsenvoi: missing BrokerId for sms id " + sms.getId());
				// NB: brokerId may null the first time we do AckStatusSmsenvoi since the SMS may not be sent yet
				// in that case we will retry next time
				// After some seconds, it really is an error. But we can wait until "nbHoursBeforeGivingUp" before detecting the error
				status = "ERROR";
			} else {
				logger.info(String.format("After %.1f hours smsenvoi smsuapi still return %s for " + sms.getId() + ":" + sms.getPhone() + ", giving up (marking as ERROR)", h, status));
				status = "ERROR";
			}
		}
		SmsStatus s = SmsStatus.valueOf(status);
		sms.setStateAsEnum(s);
		sms.setAckDate(new Date()); // update acknowledge date
		daoService.updateSms(sms);
	}
	
	/*******************
	 * Mutator.
	 *******************/

	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	public void setRequestSmsenvoi(final RequestSmsenvoi requestSmsenvoi) {
		this.requestSmsenvoi = requestSmsenvoi;
	}

	public void setCheckdelivery_url(String checkdelivery_url) {
		this.checkdelivery_url = checkdelivery_url;
	}

	/**
	 * Standard setter used by spring.
	 * @param nbHoursBeforeGivingUp
	 */
	public void setNbHoursBeforeGivingUp(final double nbHoursBeforeGivingUp) {
		this.nbHoursBeforeGivingUp = nbHoursBeforeGivingUp;
	}	
	

}
