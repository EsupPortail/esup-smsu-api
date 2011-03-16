package org.esupportail.smsuapi.services.scheduler.job;

import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.business.SendSmsThirdManager;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author PRQD8824
 *
 */
public class SuperviseSmsSendingByThird extends AbstractQuartzJob {

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * The quartz job name associated (by defaults it is the application bean name).
	 */
	public static final String SUPERVISE_SMS_SENDING_JOB_NAME = "superviseSmsSendingByThird";
	
	public static final String SUPERVISE_SMS_BROKER_KEY = "superviseSmsBrokerKey";
	
	private static final String SEND_SMS_MANAGER_BEAN_NAME = "sendSmsThirdManager";
	

	@SuppressWarnings("unchecked")
	@Override
	protected void executeJob(final ApplicationContext applicationContext, final JobDataMap jobDataMap) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Launching Quartz task SuperviseSmsSendingByThird now");
		}
		
		final SendSmsThirdManager sendSmsThirdManager = (SendSmsThirdManager) applicationContext.getBean(SEND_SMS_MANAGER_BEAN_NAME);
		
		List<SMSBroker> smsMessageList = (List<SMSBroker>)jobDataMap.get(SUPERVISE_SMS_BROKER_KEY);
		
		for (SMSBroker smsMessage : smsMessageList) {
		sendSmsThirdManager.sendWaitingForSendingSms(smsMessage);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of Quartz task SuperviseSmsSendingByThird");
		}
	}

}
