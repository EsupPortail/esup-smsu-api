package org.esupportail.smsuapi.services.sms.ackmanagement;

import java.util.Date;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.HibernateDaoServiceImpl;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Blacklist;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;

/**
 *  
 * @author PRQD8824
 */
public class AckManagerBusiness {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Hibernate template. 
	 */
	private DaoService daoService;

	/**
	 * Business layer to mange ack.
	 * @param acknowledgment
	 */
	public void manageAck(final Acknowledgment acknowledgment) {
		final int smid = acknowledgment.getSmsId();
		final SmsStatus smsStatus = acknowledgment.getSmsStatus();
		
		if (logger.isDebugEnabled()) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Manage ack with : ");
			stringBuilder.append(" - smid : ").append(smid);
			stringBuilder.append(" - ack status : ").append(smsStatus);
			logger.debug(stringBuilder.toString());
		}
			
		final Sms sms = daoService.getSms(smid);
		
		if (sms != null) {
			if (logger.isDebugEnabled()) {
				final StringBuilder message = new StringBuilder();
				message.append("Updating in DB SMS with : ");
				message.append(" - sms_id : ").append(smid);
				logger.debug(message.toString());
			}
			
			sms.setStateAsEnum(smsStatus);
			daoService.updateSms(sms);
			
			// if
			if (smsStatus.equals(SmsStatus.ERROR_POST_BL)) {
				final Application application = sms.getApp();
				final String phoneNumber = sms.getPhone();
				putPhoneNumberInBlackList(phoneNumber, application);
			}
			
		} else {
			final StringBuilder message = new StringBuilder();
			message.append("unable to find in db sms with : ");
			message.append(" - sms_id : ").append(smid);
			message.append("In order to update is state in DB").append(smid);
			logger.error(message.toString());
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
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Adding to black list : \n");
				sb.append(" - Phone number : ").append(phoneNumber).append("\n");
				sb.append(" - Application id : ").append(application.getId()).append("\n");
				sb.append(" - Date : ").append(currentDate);
				logger.debug(sb.toString());
			}
			
			daoService.addBlacklist(blackList);
		}
	}
	
	
	
	/***
	 * Mutator
	 */
	
	
	/**
	 * Standard setter used by spring.
	 * @param hibernateDaoServiceImpl
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
	
}
