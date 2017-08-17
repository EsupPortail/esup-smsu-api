package org.esupportail.smsuapi.services.sms.impl.olm;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.services.sms.ackmanagement.AckManager;
import org.esupportail.smsuapi.services.sms.ackmanagement.Acknowledgment;
import javax.inject.Inject;

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
	private final Logger logger = Logger.getLogger(getClass());
	
	@Inject private AckManager ackManager;

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
			logger.debug("Invoking manage with parameter DeliveryReceipt method" + 
				     "  - smid : " + result.getSmid() + 
				     "  - msmisdn : " + result.getMsisdn() + 
				     "  - date : " + result.getDate() + 
				     "  - status : " + result.getStatus() + 
				     "  - message : " + result.getMessage() + 
				     "  - cause : " + result.getCause() + 
				     "  - country code : " + result.getCountryCode() + 
				     "  - operator : " + result.getOperator() + 
				     "  - pnm retry number : " + result.getPNMRetryNumber());
		}
		
		final int rcpStatus = result.getStatus();
		final int rcpCause = result.getCause();
		
		final Acknowledgment acknowledgment = new Acknowledgment();

		// common elements
		acknowledgment.setSmsId(result.getSmid());
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
        logger.debug("Invoking manage with parameter MMSMO method");
		return true;
	}
	

	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.SMMO)
	 */
	public boolean manage(final SMMO smMO) {
        logger.debug("Invoking manage with parameter SMMO method");
		return true;
	}

	
	
	/* (non-Javadoc)
	 * @see fr.cvf.util.mgs.mode.sgs.Manager#manage(fr.cvf.util.mgs.mode.sgs.message.response.Ack)
	 */
	public void manage(final Ack ack) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Receiving ack : " + 
				     "  - id : " + ack.getId() + 
				     "  - type : " + ack.getType() + 
				     "  - count : " + ack.getCount());
		}
		
		if (SMText.EXPECTED_ACK_TYPE.equalsIgnoreCase(ack.getType())) {
			//
			
		} else {
			logger.error("Ack received is not and SMText ack : " + 
				     "  - id : " + ack.getId() + 
				     "  - type : " + ack.getType() + 
				     "  - count : " + ack.getCount() + 
				     "Ack not managed ");
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
	
}
