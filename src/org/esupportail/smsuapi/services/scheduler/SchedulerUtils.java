package org.esupportail.smsuapi.services.scheduler;

import java.util.Date;
import java.util.List;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.scheduler.job.SuperviseSmsSending;
import org.esupportail.smsuapi.services.scheduler.job.SuperviseSmsSendingByThird;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

/**
 * Provide tools to manage quartz tasks.
 * @author PRQD8824
 *
 */
public class SchedulerUtils {

	/**
     * logger.
     */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Quartz scheduler.
	 */
	private Scheduler scheduler;

	
	public void launchSuperviseSmsSending(final SMSBroker smsMessage) {
		// use hashCode of smsMessage
		
		final long now = System.currentTimeMillis();
		
		final String jobName = "superviseSmsSending"+now;
		final String keyName = SuperviseSmsSending.SUPERVISE_SMS_BROKER_KEY;
		final String groupName = Scheduler.DEFAULT_GROUP;
   		
		try {
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(300);
				sb.append("smsMessage in launchSuperviseSmsSending is : ");
				sb.append(" - smsMessage id is : ").append(smsMessage.getId());	
				sb.append(" - smsMessage content is : ").append(smsMessage.getMessage());
				sb.append(" - smsMessage phone is : ").append(smsMessage.getRecipient());
				logger.debug(sb.toString());
			 }
			
			// create DataMap
			final JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(keyName, smsMessage);
		
			// create trigger
			final Trigger trigger = new SimpleTrigger(jobName, groupName);

			trigger.setVolatility(false);
	        trigger.setStartTime(new Date(now));


			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Launching job with parameter : \n");
				sb.append(" - jobName : ").append(jobName).append("\n");
				sb.append(" - groupName : ").append(groupName).append("\n");
				logger.debug(sb.toString());
			}
			
			JobDetail jobDetail = new JobDetail(jobName, groupName, SuperviseSmsSending.class);
			jobDetail.setJobDataMap(jobDataMap);
			
			scheduler.scheduleJob(jobDetail, trigger);
			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Job successfully launched");
				logger.debug(sb.toString());
				
			}
		} catch (SchedulerException e) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("An error occurs launching the job with parameter : \n");
			sb.append(" - jobName : ").append(jobName).append("\n");
			sb.append(" - groupName : ").append(groupName).append("\n");
			
			logger.warn(sb.toString());
		}
	}

	
	public void launchSuperviseSmsSendingByThird(final List<SMSBroker> smsMessageList) {
		// use hashCode of smsMessage
		
		final long now = System.currentTimeMillis();
		
		final String jobName = "superviseSmsSendingByThird"+now;
		final String keyName = SuperviseSmsSendingByThird.SUPERVISE_SMS_BROKER_KEY;
		final String groupName = Scheduler.DEFAULT_GROUP;
   		
		try {
			
			for (SMSBroker smsMessage : smsMessageList) {
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(300);
				sb.append("smsMessage in launchSuperviseSmsSendingByThird is : ");
				sb.append(" - smsMessage id is : ").append(smsMessage.getId());	
				sb.append(" - smsMessage content is : ").append(smsMessage.getMessage());
				sb.append(" - smsMessage phone is : ").append(smsMessage.getRecipient());
				logger.debug(sb.toString());
			 }
			}
			
			// create DataMap
			final JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(keyName, smsMessageList);
		
			// create trigger
			final Trigger trigger = new SimpleTrigger(jobName, groupName);

			trigger.setVolatility(false);
	        trigger.setStartTime(new Date(now));


			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Launching job with parameter : \n");
				sb.append(" - jobName : ").append(jobName).append("\n");
				sb.append(" - groupName : ").append(groupName).append("\n");
				logger.debug(sb.toString());
			}
			
			JobDetail jobDetail = new JobDetail(jobName, groupName, SuperviseSmsSendingByThird.class);
			jobDetail.setJobDataMap(jobDataMap);
			
			scheduler.scheduleJob(jobDetail, trigger);
			
			if (logger.isDebugEnabled()) {
				final StringBuilder sb = new StringBuilder(200);
				sb.append("Job successfully launched");
				logger.debug(sb.toString());
				
			}
		} catch (SchedulerException e) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("An error occurs launching the job with parameter : \n");
			sb.append(" - jobName : ").append(jobName).append("\n");
			sb.append(" - groupName : ").append(groupName).append("\n");
			
			logger.warn(sb.toString());
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
