package org.esupportail.smsuapi.services.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.scheduler.job.SuperviseSmsSending;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;

/**
 * Provide tools to manage quartz tasks.
 * @author PRQD8824
 *
 */
public class SchedulerUtils {

	/**
     * logger.
     */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Quartz scheduler.
	 */
	private Scheduler scheduler;

	
	public void launchSuperviseSmsSending(final SMSBroker smsMessageList) {
		// use hashCode of smsMessage
		
		final long now = System.currentTimeMillis();
		
		final String jobName = "superviseSmsSending"+now;
		final String keyName = SuperviseSmsSending.SUPERVISE_SMS_BROKER_KEY;
		final String groupName = Scheduler.DEFAULT_GROUP;
   		
		try {
			if (logger.isDebugEnabled()) {
		    for (SMSBroker.Rcpt smsMessage : smsMessageList.rcpts) {
				logger.debug("smsMessage in launchSuperviseSmsSending is : " + 
					     " - smsMessage id is : " + smsMessage.id + 
					     " - smsMessage content is : " + smsMessageList.message + 
					     " - smsMessage phone is : " + smsMessage.recipient);
			 }
		  }
			
			// create DataMap
			final JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(keyName, smsMessageList);
		
			// create trigger
			final Trigger trigger = newTrigger().withIdentity(jobName, groupName).build();
			
			if (logger.isDebugEnabled()) {
				logger.debug("Launching job with parameter : \n" + 
					     " - jobName : " + jobName + "\n" + 
					     " - groupName : " + groupName + "\n");
			}
			
			JobDetail jobDetail = newJob(SuperviseSmsSending.class)
                .withIdentity(jobName, groupName)
			    .usingJobData(jobDataMap).build();
			
			scheduler.scheduleJob(jobDetail, trigger);
			
            logger.debug("Job successfully launched");
		} catch (SchedulerException e) {
			logger.warn("An error occurs launching the job with parameter : \n" + 
				    " - jobName : " + jobName + "\n" + 
				    " - groupName : " + groupName + "\n", e);
		}
	}
		
	
	/**
	 * Standard setter used by spring.
	 * @param scheduler
	 */
	public void setScheduler(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	
}
