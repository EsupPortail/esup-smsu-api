package org.esupportail.smsuapi.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;

/**
 * Manage the purge in table SMS.
 * @author PRQD8824
 *
 */
public class PurgeSms {

	/**
	 * Maximum day of sms seniority.
	 */
	private int seniorityDay;
	
	/**
	 * Used to manage db.
	 */
	private DaoService daoService;

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	/**
	 * 
	 */
	public void purgeSms() {
		// compute de limite date
		final long currentTimeInMillis = System.currentTimeMillis();
		final Calendar seniorityDateAsCal = new GregorianCalendar();
		seniorityDateAsCal.setTimeInMillis(currentTimeInMillis);
		
		seniorityDateAsCal.add(Calendar.DAY_OF_YEAR, -seniorityDay);
		
		final Date seniorityDateAsDate = seniorityDateAsCal.getTime();
		
		purgeSmsOlderThan(seniorityDateAsDate);
	}
	
	/**
	 * Purge the Sms in db with date older than the specified date.
	 * @param date
	 */
	private void purgeSmsOlderThan(final Date date) {
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("Starting purge of SMS table with parameter : \n");
			sb.append(" - date : ").append(date);
			logger.debug(sb.toString());
		}
		
		final int nbSmsDeleted = daoService.deleteSmsOlderThan(date);
		
		if (logger.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder(200);
			sb.append("End purge of SMS table, result : \n");
			sb.append(" - number of sms deleted : ").append(nbSmsDeleted);
			logger.debug(sb.toString());
		}
	}
	
	
	/******************
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
