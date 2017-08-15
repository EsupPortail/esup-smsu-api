package org.esupportail.smsuapi.services.scheduler.job;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.business.stats.StatisticBuilder;
import org.esupportail.smsuapi.services.scheduler.AbstractQuartzJob;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

/**
 * This job launch the statistic generation.
 * @author PRQD8824
 *
 */
public class BuildStatisticsJob extends AbstractQuartzJob {

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext, final JobDataMap jobDataMap) {
        logger.debug("Launching Quartz task BuildStatisticsJob now");
		
		final StatisticBuilder statisticBuilder = (StatisticBuilder) applicationContext.getBean("statisticBuilder");
		statisticBuilder.buildAllStatistics();

        logger.debug("End of Quartz task BuildStatisticsJob");
	}

}
