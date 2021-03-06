package org.esupportail.smsuapi.services.scheduler.job;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.business.SendSmsManager;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author PRQD8824
 *
 */
public class SuperviseSmsSending extends AbstractQuartzJob {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * The quartz job name associated (by defaults it is the application bean name).
	 */
	public static final String SUPERVISE_SMS_SENDING_JOB_NAME = "superviseSmsSending";
	
	public static final String SUPERVISE_SMS_BROKER_KEY = "superviseSmsBrokerKey";
	
	private static final String SEND_SMS_MANAGER_BEAN_NAME = "sendSmsManager";
	

	@Override
	protected void executeJob(final ApplicationContext applicationContext, final JobDataMap jobDataMap) {
        logger.debug("Launching Quartz task SuperviseSmsSending now");
		
		final SendSmsManager sendSmsManager = (SendSmsManager) applicationContext.getBean(SEND_SMS_MANAGER_BEAN_NAME);
		
		SMSBroker smsMessageList = (SMSBroker) jobDataMap.get(SUPERVISE_SMS_BROKER_KEY);

			sendSmsManager.sendWaitingForSendingSms(smsMessageList);

		
        logger.debug("End of Quartz task SuperviseSmsSending");
	}

}
