package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the acknoledge status from smsenvoi job
 *
 */
public class AckStatusSmsenvoiJob extends AbstractQuartzJob {
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext,	final JobDataMap jobDataMap) {
		((AckStatusSmsenvoi) applicationContext.getBean("ackStatusSmsenvoi")).smsuapiStatus();
	}
}
