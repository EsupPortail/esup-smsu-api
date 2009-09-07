package org.esupportail.smsuapi.services.scheduler.job;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
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
	private final Logger logger = new LoggerImpl(getClass());
	
	
	@Override
	protected void executeJob(final ApplicationContext applicationContext, final JobDataMap jobDataMap) {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("Launching Quartz task BuildStatisticsJob now");
			logger.debug(sb.toString());
		}
		
		final StatisticBuilder statisticBuilder = (StatisticBuilder) applicationContext.getBean("statisticBuilder");
		statisticBuilder.buildAllStatistics();

		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(100);
			sb.append("End of Quartz task BuildStatisticsJob");
			logger.debug(sb.toString());
		}
			
	}

}
