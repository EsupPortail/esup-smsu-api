package org.esupportail.smsuapi.services.sms.impl.proxy;

import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the acknoledge status from proxy job
 *
 */
public class AckStatusProxyJob extends AbstractQuartzJob {
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext,	final JobDataMap jobDataMap) {
		((AckStatusProxy) applicationContext.getBean("ackStatusProxy")).smsuapiStatus();
	}
}
