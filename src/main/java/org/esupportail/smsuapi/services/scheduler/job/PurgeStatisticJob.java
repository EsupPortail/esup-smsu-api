package org.esupportail.smsuapi.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.business.purge.PurgeStatistic;
import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the statistic table purge.
 * @author PRQD8824
 *
 */
public class PurgeStatisticJob extends AbstractQuartzJob {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext, final JobDataMap jobDataMap) {
		if (logger.isDebugEnabled()) {
			logger.debug("Launching Quartz task PurgeStatisticJob now");
		}
		
		final PurgeStatistic purgeStatistic = (PurgeStatistic) applicationContext.getBean("purgeStatistic");
		purgeStatistic.purgeStatistic();
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("End of Quartz task PurgeStatisticJob");
		} 
	}
	
}
