package org.esupportail.smsuapi.business.stats;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.dao.beans.Statistic;
import org.esupportail.smsuapi.dao.beans.StatisticPK;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class manage the statistic creation.
 * @author PRQD8824
 *
 */
public class StatisticBuilder {

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());


	/**
	 * Use to get database informations.
	 */
	@Autowired private DaoService daoService;

	/**
	 * Build all non already computed statistic whatever the application, account or date.
	 */
	public void buildAllStatistics() {

		final List<Map<String,?>> list = daoService.getAppsAndCountsToTreat();
		
		for (Map<String,?> map : list) {
			
			// get the date of older SMS for this app and account
			final Application application = (Application) map.get(Sms.PROP_APP);
			final Account account = (Account) map.get(Sms.PROP_ACC);
			final Date olderSmsDate = daoService.getDateOfOlderSmsByApplicationAndAccount(application, account);
			// if there is not at least 1 sms in db for the specified app / account, the 
			// previous method returns null, so we have to check it.
			if (olderSmsDate != null) {
				// get the list of month where stats was not computed since the date of the older SMS in DB
				final List<Date> monthToComputeStatsList = getListOfMarkerDateForNonComputedStats(application, account, olderSmsDate);
				for (Date monthToComputeStats : monthToComputeStatsList) {
					// compute the stats for this specific app, account and month
					buildStatisticForAMonth(application, account, monthToComputeStats);
				}
			}

		}
	}


	/**
	 * Create stats in DB for the specified application and account for the month.
	 * @param application
	 * @param account
	 * @param month
	 */
	public void buildStatisticForAMonth(final Application application, final Account account,
			final Date month) {

		final Date firstDayOfMonth = getFirstDayOfMonth(month);
		final Date lastDayOfMonth = getLastDayOfMonth(month);
		final Date markerDate = getMarkerDateOfMonth(month);

		buildStatistics(application, account, firstDayOfMonth, lastDayOfMonth, markerDate);
	}

	/**
	 * 
	 * @param application
	 * @param account
	 * @param beginDate
	 * @param endDate
	 * @param markerDate is the field in db called stat_month
	 */
	private void buildStatistics(final Application application, final Account account,
			final Date beginDate, final Date endDate, 
			final Date markerDate) {
		if (logger.isDebugEnabled()) {
			logger.debug("Start building statistic with parameters : \n" + 
				     " - application id : " + application.getId() + "\n" + 
				     " - account id : " + account.getId() + "\n" + 
				     " - begin date : " + beginDate + "\n" + 
				     " - end date : " + endDate + "\n" + 
				     " - marker date : " + markerDate);
		}

		// get stat values from DB
		final int nbSms = daoService.getNbOfSmsByAppAndAccountAndDate(application, account, beginDate, endDate);
		if (nbSms > 0) {
			final int nbSmsInError = daoService.getNbOfSmsInErrorByAppAndAccountAndDate(application, account, beginDate, endDate);

			final Long nbSmsAsLong = new Long(nbSms);
			final Long nbSmsInErrorAsLong = new Long(nbSmsInError);

			// Building the PK
			final StatisticPK statisticPK = new StatisticPK();
			statisticPK.setApp(application);
			statisticPK.setAcc(account);
			statisticPK.setMonth(markerDate);

			// Building the stats
			final Statistic statistic = new Statistic();
			statistic.setId(statisticPK);
			statistic.setNbSms(nbSmsAsLong);
			statistic.setNbSmsInError(nbSmsInErrorAsLong);

			// store it in DB
			daoService.addStatistic(statistic);

			if (logger.isDebugEnabled()) {
				logger.debug("End of building statistic, result : \n" + 
					     " - application id : " + application.getId() + "\n" + 
					     " - account id : " + account.getId() + "\n" + 
					     " - marker date : " + markerDate + "\n" + 
					     " - Nb sms : " + nbSmsAsLong + "\n" + 
					     " - Nb sms in error : " + nbSmsInErrorAsLong);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No statistic result : \n" + 
					     " - application id : " + application.getId() + "\n" + 
					     " - account id : " + account.getId() + "\n" + 
					     " - marker date : " + markerDate + "\n" + 
					     " - No SMS ");	
			}
		}
	}



		/**
		 * Return the first day of month at 00h00.
		 * @param date
		 * @return
		 */
		private Date getFirstDayOfMonth(final Date date) {
			final Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			final Date retVal = calendar.getTime();
			return retVal;
		}

		/**
		 * Return the last day of month at 23h59.
		 * @param date
		 * @return
		 */
		private Date getLastDayOfMonth(final Date date) {
			final Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			final int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			final Date retVal = calendar.getTime();
			return retVal;
		}

		/**
		 * Return the date to use in db in column stat_month of table statistic for a specific month.
		 * @param date
		 * @return
		 */
		private Date getMarkerDateOfMonth(final Date date) {
			final Date retVal = getFirstDayOfMonth(date); 
			return retVal;
		}


		/**
		 * Return the list of all missing marker date in table statistic for a specific application
		 * and accout between the given date and (now - 1 month).
		 * @param application
		 * @param account
		 * @param startDate
		 * @return
		 */
		private List<Date> getListOfMarkerDateForNonComputedStats(final Application application, 
				final Account account,
				final Date startDate) {


			final List<Date> retVal = new LinkedList<>();

			final long nowInMillis = System.currentTimeMillis();
			final Calendar previousMonthAsCal = new GregorianCalendar();
			previousMonthAsCal.setTimeInMillis(nowInMillis);
			final int previusMonthIndex = previousMonthAsCal.get(Calendar.MONTH) - 1;
			previousMonthAsCal.set(Calendar.MONTH, previusMonthIndex);

			final Date previousMonth = previousMonthAsCal.getTime();

			final Date endDate = getLastDayOfMonth(previousMonth);

			final List<Date> markerDateList = getListOfMarkerDateBetWeenTwoDates(startDate, endDate);

			for(Date markerDate : markerDateList) {
				final boolean statAlreadyComputed = daoService.isStatisticExistsForApplicationAndAccountAndDate(application, 
						account, 
						markerDate);

				if (!statAlreadyComputed) {
					retVal.add(markerDate);
				}

			}

			return retVal;


		}

		/**
		 * Return the list of all potential marker date between two dates.
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		private List<Date> getListOfMarkerDateBetWeenTwoDates(final Date startDate, final Date endDate) {

			final List<Date> listOfMarkerDate = new LinkedList<>();

			final Calendar startDateAsCal = new GregorianCalendar();
			startDateAsCal.setTime(startDate);

			final Calendar endDateAsCal = new GregorianCalendar();
			endDateAsCal.setTime(endDate);

			while (startDateAsCal.before(endDateAsCal)) {
				final Date markerDate = getMarkerDateOfMonth(startDateAsCal.getTime());
				listOfMarkerDate.add(markerDate);

				startDateAsCal.add(Calendar.MONTH, 1);
			}

			return listOfMarkerDate;
		}

	}
