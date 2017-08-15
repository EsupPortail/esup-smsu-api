package org.esupportail.smsuapi.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Autowired private DaoService daoService;

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
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
		
			logger.debug("Starting purge of SMS table with parameter : \n" + " - date : " + date);
		
		final int nbSmsDeleted = daoService.deleteSmsOlderThan(date);
		
			logger.debug("End purge of SMS table, result : \n" + 
				     " - number of sms deleted : " + nbSmsDeleted);
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
	
}
