package org.esupportail.smsuapi.business.purge;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import javax.inject.Inject;

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
	@Inject private DaoService daoService;

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	public void purgeSms() {
		purgeSmsOlderThan(getNowMinusDays(seniorityDay));
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
    
    private Date getNowMinusDays(int days) {
		// compute de limite date
		final Calendar seniorityDateAsCal = new GregorianCalendar();
		seniorityDateAsCal.setTimeInMillis(System.currentTimeMillis());
		seniorityDateAsCal.add(Calendar.DAY_OF_YEAR, -days);
		
        return seniorityDateAsCal.getTime();
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
