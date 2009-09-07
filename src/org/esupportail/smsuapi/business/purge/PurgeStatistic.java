package org.esupportail.smsuapi.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;

/**
 * Manage the purge in table statistic. 
 * @author PRQD8824
 *
 */
public class PurgeStatistic {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * Maximum day of statistic seniority.
	 */
	private int seniorityDay;
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;

	/**
	 * 
	 */
	public void purgeStatistic() {
		// compute de limite date
		final long currentTimeInMillis = System.currentTimeMillis();
		final Calendar seniorityDateAsCal = new GregorianCalendar();
		seniorityDateAsCal.setTimeInMillis(currentTimeInMillis);
		
		seniorityDateAsCal.add(Calendar.DAY_OF_YEAR, -seniorityDay);
		
		final Date seniorityDateAsDate = seniorityDateAsCal.getTime();
		
		purgeStatisticOlderThan(seniorityDateAsDate);
	}
	
	/**
	 * Purge the statistic in db with date older than the specified date.
	 * @param date
	 */
	private void purgeStatisticOlderThan(final Date date) {
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Starting purge of statistic table with parameter : \n");
			sb.append(" - date : ").append(date);
			logger.debug(sb.toString());
		}
		
		final int nbStatisticDeleted = daoService.deleteStatisticOlderThan(date);
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End purge of statistic table, result : \n");
			sb.append(" - number of statistic deleted : ").append(nbStatisticDeleted);
			logger.debug(sb.toString());
		}
	}
	
		
	
	/**********************
	 * Mutator
	 */
	
	/**
	 * Standard setter used by spring.
	 * @param seniorityDay
	 */
	public void setSeniorityDay(final int seniorityDay) {
		this.seniorityDay = seniorityDay;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param daoService
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}
}
