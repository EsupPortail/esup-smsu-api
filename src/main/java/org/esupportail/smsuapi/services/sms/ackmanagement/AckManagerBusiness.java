package org.esupportail.smsuapi.services.sms.ackmanagement;

import java.util.Date;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Blacklist;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  
 * @author PRQD8824
 */
public class AckManagerBusiness {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired private DaoService daoService;

	/**
	 * Business layer to mange ack.
	 * @param acknowledgment
	 */
	public void manageAck(final Acknowledgment acknowledgment) {
		final int smid = acknowledgment.getSmsId();
		final SmsStatus smsStatus = acknowledgment.getSmsStatus();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Manage ack with : " + 
				     " - smid : " + smid + 
				     " - ack status : " + smsStatus);
		}
			
		final Sms sms = daoService.getSms(smid);
		
		if (sms != null) {
			manageAck(sms, smsStatus);			
		} else {
			logger.error("unable to find in db sms with : " + 
				     " - sms_id : " + smid + 
				     "In order to update is state in DB" + smid);
		}
		
	}

	private void manageAck(final Sms sms, final SmsStatus smsStatus) {
        logger.debug("Updating in DB SMS with : " + " - sms_id : " + sms.getId());

		sms.setStateAsEnum(smsStatus);
		sms.setAckDate(new Date());
		daoService.updateSms(sms);
				
		if (smsStatus.equals(SmsStatus.ERROR_POST_BL)) {
			putPhoneNumberInBlackList(sms.getPhone(), sms.getApp());
		}
	}
	
	/**
	 * Put a number in black list.
	 * @param phoneNumber
	 * @param application
	 */
	private void putPhoneNumberInBlackList(final String phoneNumber, final Application application ) {
		final boolean isPhoneNumberAlreadyInBL = daoService.isPhoneNumberInBlackList(phoneNumber);
		
		// if phone number is not already in the bl (it should not append) put the phone number
		// put the phone number in the black list
		if (! isPhoneNumberAlreadyInBL) {
			
			final Blacklist blackList = new Blacklist();
			final Date currentDate = new Date(System.currentTimeMillis());
			blackList.setApp(application);
			blackList.setDate(currentDate);
			blackList.setPhone(phoneNumber);
						
			if (logger.isDebugEnabled()) {
				logger.debug("Adding to black list : \n" + 
					     " - Phone number : " + phoneNumber + "\n" + 
					     " - Application id : " + application.getId() + "\n" + 
					     " - Date : " + currentDate);
			}
			
			daoService.addBlacklist(blackList);
		}
	}
	
}
