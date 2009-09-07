package org.esupportail.smsuapi.services.sms.impl.olm;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.services.sms.ackmanagement.AckManager;
import org.esupportail.smsuapi.services.sms.ackmanagement.Acknowledgment;

import fr.cvf.util.mgs.mode.sgs.Manager;
import fr.cvf.util.mgs.mode.sgs.message.request.SMText;
import fr.cvf.util.mgs.mode.sgs.message.response.Ack;
import fr.cvf.util.mgs.mode.sgs.message.response.DeliveryReceipt;
import fr.cvf.util.mgs.mode.sgs.message.response.MMSMO;
import fr.cvf.util.mgs.mode.sgs.message.response.SMMO;

/**
 * Ack manager for olm impl.
 * @author PRQD8824
 *
 */
public class OlmAckManager extends Manager {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 *  
	 */
	private AckManager ackManager;

	/**
	 * Success status value. 
	 */
	private int olmSentStatusCode;
	
	/**
	 * Error status value.
	 */
	private int olmErrorStatusCode;
	
	/**
	 * Invalid msisdn code value (case of MSISDN to put in BL).
	 */
	private int olmInvalidMSISDNCauseCode;
		

	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.DeliveryReceipt)
	 */
	public boolean manage(final DeliveryReceipt result) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder message = new StringBuilder();
			message.append("Invoking manage with parameter DeliveryReceipt method");
			message.append("  - smid : ").append(result.getSmid());
			message.append("  - msmisdn : ").append(result.getMsisdn());
			message.append("  - date : ").append(result.getDate());
			message.append("  - status : ").append(result.getStatus());
			message.append("  - message : ").append(result.getMessage());
			message.append("  - cause : ").append(result.getCause());
			message.append("  - country code : ").append(result.getCountryCode());
			message.append("  - operator : ").append(result.getOperator());
			message.append("  - pnm retry number : ").append(result.getPNMRetryNumber());
			logger.debug(message.toString());
		}
		
		final int rcpSmid = result.getSmid();
		final int rcpStatus = result.getStatus();
		final int rcpCause = result.getCause();
		
		final Acknowledgment acknowledgment = new Acknowledgment();

		// common elements
		acknowledgment.setSmsId(rcpSmid);
		// Ok case
		if (rcpStatus == olmSentStatusCode) {
			acknowledgment.setSmsStatus(SmsStatus.DELIVERED);
		// Error invalid number case (msisdn to put in bl)	
		} else if (rcpStatus == olmErrorStatusCode	
				&& rcpCause == olmInvalidMSISDNCauseCode) {
			acknowledgment.setSmsStatus(SmsStatus.ERROR_POST_BL);
		// Other error
		} else {
			acknowledgment.setSmsStatus(SmsStatus.ERROR);
		}
		
		// managing ack
		ackManager.manageAck(acknowledgment);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.MMSMO)
	 */
	public boolean manage(final MMSMO mmsMO) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking manage with parameter MMSMO method");
		}
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.SMMO)
	 */
	public boolean manage(final SMMO smMO) {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking manage with parameter SMMO method");
		}
		
		return true;
	}

	
	
	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.Ack)
	 */
	public void manage(final Ack ack) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder message = new StringBuilder();
			message.append("Receiving ack : ");
			message.append("  - id : ").append(ack.getId());
			message.append("  - type : ").append(ack.getType());
			message.append("  - count : ").append(ack.getCount());
			logger.debug(message.toString());
		}
		
		if (SMText.EXPECTED_ACK_TYPE.equalsIgnoreCase(ack.getType())) {
			//
			
		} else {
			final StringBuilder message = new StringBuilder();
			message.append("Ack received is not and SMText ack : ");
			message.append("  - id : ").append(ack.getId());
			message.append("  - type : ").append(ack.getType());
			message.append("  - count : ").append(ack.getCount());
			message.append("Ack not managed ");
			logger.error(message.toString());
		}
		
	 }
	
	/**********************
	 * Mutator.
	 */
	
	/**
	 * Standard setter used by Spring.
	 */
	public void setOlmSentStatusCode(final int olmSentStatusCode) {
		this.olmSentStatusCode = olmSentStatusCode;
	}

	/**
	 * Standard setter used by Spring.
	 * @param olmErrorStatusCode
	 */
	public void setOlmErrorStatusCode(final int olmErrorStatusCode) {
		this.olmErrorStatusCode = olmErrorStatusCode;
	}

	/**
	 * Standard setter used by Spring.
	 * @param olmInvalidMSISDNCauseCode
	 */
	public void setOlmInvalidMSISDNCauseCode(final int olmInvalidMSISDNCauseCode) {
		this.olmInvalidMSISDNCauseCode = olmInvalidMSISDNCauseCode;
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param ackManager
	 */
	public void setAckManager(final AckManager ackManager) {
		this.ackManager = ackManager;
	}

}
