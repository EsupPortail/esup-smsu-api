/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.dao;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Blacklist;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.dao.beans.Statistic;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.TimestampType;


/**
 * The Hibernate implementation of the DAO service.
 */
public class DaoService {
	
	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

	/**
	 * Bean constructor.
	 */
	public DaoService() {
		super();
	}

	/**
	 * retrieve the current session.
	 * @return
	 */
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}


	//////////////////////////////////////////////////////////////
	// WS sendSMS methods tools. 
	//////////////////////////////////////////////////////////////

	/**
	 * used by sendSMS.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public Account getAccByLabel(final String labelAccount) {
		var query = getCurrentSession().createQuery("""
		    FROM Account
			WHERE Label = :Label
		""", Account.class);
		query.setParameter(Account.PROP_LABEL, labelAccount);
		List<Account> acc = query.list(); 
		if (acc.size() != 0) { return acc.get(0);
		} else { return null; }
		
	}
	

	/**
	 * used by sendSMS.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	public int getBlackLListByPhone(final String phone) {
		var query = getCurrentSession().createQuery("""
		    SELECT count(*)
			FROM Blacklist 
			WHERE Phone = :Phone
		""", Long.class);
		query.setParameter(Blacklist.PROP_BLA_PHONE, phone);
		return query.getSingleResult().intValue();
	}

	//////////////////////////////////////////////////////////////
	// WS SendTrack methods
	//////////////////////////////////////////////////////////////

	/**
	 * @return the number of SMS recipients.
	 */
	public int getNbDest(final Integer msgId, final Application app) {
			var query = getCurrentSession().createQuery("""
			    SELECT count(*)
				FROM Sms
				WHERE InitialId = :InitialId AND App = :App
			""", Long.class);
			query.setParameter(Sms.PROP_INITIAL_ID, msgId);
			query.setParameter(Sms.PROP_APP, app);
			return query.getSingleResult().intValue();
	}
	
	/**
	 * @return the number of sms having one of the wanted states
	 */
	public int getNbSmsWithState(final Integer msgId, final Application app, final List<String> list) {
			var query = getCurrentSession().createQuery("""
			    SELECT count(*)
				FROM Sms
				WHERE InitialId = :InitialId AND App = :App AND State in :States
			""", Long.class);
			query.setParameter(Sms.PROP_INITIAL_ID, msgId);
			query.setParameter(Sms.PROP_APP, app);
			query.setParameter("States", list);
			return query.getSingleResult().intValue();
	}
	
	/**
	 * @return the number of sent SMS.
	 */
	public int getNbSentSMS(final Integer msgId, final Application app) {
	    return getNbSmsWithState(msgId, app, Collections.singletonList(SmsStatus.DELIVERED.name()));
	}
	
	/**
	 * @return the number of SMS in progress.
	 */
	public int getNbProgressSMS(final Integer msgId, final Application app) {
	    return getNbSmsWithState(msgId, app, Collections.singletonList(SmsStatus.IN_PROGRESS.name()));
	}
	
	/**
	 * @return the number of SMS in error.
	 */
	public int getNbErrorSMS(final Integer msgId, final Application app) {
	    return getNbSmsWithState(msgId, app, errorStatuses());
	}
	
	/**
	 * @return the list of phones SMS in error.
	 */
	public List<Sms> getListNumErreur(final Integer msgId, final Application app) {
			var query = getCurrentSession().createQuery("""
			    FROM Sms
				WHERE InitialId = :InitialId AND App = :App AND State in :States
			""", Sms.class);
			query.setParameter(Sms.PROP_INITIAL_ID, msgId);
			query.setParameter(Sms.PROP_APP, app);
			query.setParameter("States", errorStatuses());
			return query.list();  
	}

	//////////////////////////////////////////////////////////////
	// SMS
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param id
	 * @return
	 */
	public Sms getSms(final int id) {
		return (Sms) getCurrentSession().get(Sms.class, id);
	}
	
	public Sms getSmsByBrokerId(String id) {
		Session currentSession = getCurrentSession();
		var query = currentSession.createQuery("""
		    FROM Sms
			WHERE BrokerId = :BrokerId
		""", Sms.class);
		query.setParameter(Sms.PROP_BROKER_SMS_ID, id);
		return query.getSingleResult();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esupportail.smsuapi.dao.DaoService#getSms(org.esupportail.smsuapi.dao.beans.Application, int, java.lang.String)
	 */
	public List<Sms> getSms(final Application app, final  int id, final String phoneNumber) {
		var query = getCurrentSession().createQuery("""
		    FROM Sms
			WHERE InitialId = :InitialId and App = :App and Phone = :Phone
		""", Sms.class);
		query.setParameter(Sms.PROP_INITIAL_ID,id);
		query.setParameter(Sms.PROP_APP,app);
		query.setParameter(Sms.PROP_PHONE, phoneNumber);
		return query.list();
	}
	
	public List<Sms> getSms(SmsStatus status) {
		var query = getCurrentSession().createQuery("""
		    FROM Sms
			WHERE State = :State
		""", Sms.class);
		query.setParameter(Sms.PROP_STATE, status.name());
		return query.list();		
	}

	public Integer getNewInitialId(Application app) {
		var query = getCurrentSession().createQuery("""
		    SELECT max(InitialId)
			FROM Sms
			WHERE App = :App
		""", Integer.class);
		query.setParameter(Sms.PROP_APP, app);

		Integer max = query.getSingleResult();
		
		return 1 + (max != null ? max : 0);
	}

	/**
	 * @param sms
	 */
	public void updateSms(final Sms sms) {
		updateObject(sms);
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getDateOfOlderSmsByApplicationAndAccount
	 * (org.esupportail.smsuapi.dao.beans.Application, org.esupportail.smsuapi.dao.beans.Account)
	 */
	public Date getDateOfOlderSmsByApplicationAndAccount(final Application application, final Account account) {
		var query = getCurrentSession().createQuery("""
		    SELECT min(Date)
			FROM Sms
			WHERE App = :App AND Acc = :Acc
		""", Date.class);
		query.setParameter(Sms.PROP_APP, application);
		query.setParameter(Sms.PROP_ACC, account);
		return query.getSingleResult();
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getNbOfSmsByAppAndAccountAndDate
	 * (org.esupportail.smsuapi.dao.beans.Application, 
	 * org.esupportail.smsuapi.dao.beans.Account, java.util.Date, java.util.Date)
	 */
	public int getNbOfSmsByAppAndAccountAndDate(final Application application, final Account account,
						final Date startDate, final Date endDate) {
		var query = getCurrentSession().createQuery("""
		    SELECT count(*)
			FROM Sms
			WHERE App = :App AND Acc = :Acc AND Date BEETWEEN :startDate and :endDate
		""", Long.class);
		query.setParameter(Sms.PROP_APP, application);
		query.setParameter(Sms.PROP_ACC, account);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getSingleResult().intValue();
	}

    public List<String> errorStatuses() {
        List<String> list = new ArrayList<>();
        list.add(SmsStatus.ERROR.name());
        list.add(SmsStatus.ERROR_PRE_BL.name());
        list.add(SmsStatus.ERROR_POST_BL.name());
        return list;
    }
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getNbOfSmsInErrorByAppAndAccountAndDate
	 * (org.esupportail.smsuapi.dao.beans.Application, org.esupportail.smsuapi.dao.beans.Account, 
	 * java.util.Date, java.util.Date)
	 */
	public int getNbOfSmsInErrorByAppAndAccountAndDate(final Application application, final Account account,
						final Date startDate, final Date endDate) {
		final Session currentSession = getCurrentSession();
		var query = currentSession.createQuery("""
		    SELECT count(*)
			FROM Sms
			WHERE App = :App AND Acc = :Acc AND Date BEETWEEN :startDate AND :endDate AND State in :states
		""", Long.class);
		query.setParameter(Sms.PROP_APP, application);
		query.setParameter(Sms.PROP_ACC, account);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("states", errorStatuses());
				          
		return query.getSingleResult().intValue();
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#deleteSmsOlderThan(java.util.Date)
	 */
	public int deleteSmsOlderThan(final Date date) {
		final String hql = "delete from Sms as sms where sms.Date < :date";
		
		var query = getCurrentSession().createQuery(hql, Sms.class);
		query.setParameter("date", date, TimestampType.INSTANCE);
		
		final int nbSmsDeleted = query.executeUpdate();
		
		return nbSmsDeleted;
	}
	
	//////////////////////////////////////////////////////////////
	// ACCOUNT
	//////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param account
	 */
	public void addAccount(final Account account) {
		addObject(account);
	}
	
	/**
	 * @param account
	 */
	public void updateAccount(final Account account) {
		updateObject(account);
	}
	
	//////////////////////////////////////////////////////////////
	// APPLICATION
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param application
	 */
	public void updateApplication(final Application application) {
		updateObject(application);
	}

	/**
	 * @see org.esupportail.smsuapi.dao.DaoService#getApplicationByName(java.lang.String)
	 */
	public Application getApplicationByName(final String name) {
		var query = getCurrentSession().createQuery("""
		    FROM Application
			WHERE Name = :Name
		""", Application.class);
		query.setParameter(Application.PROP_NAME, name);
		return query.getSingleResult();
	}

	//////////////////////////////////////////////////////////////
	// Black list
	//////////////////////////////////////////////////////////////
	

	/**
	 * 
	 * @param blackList
	 */
	public void addBlacklist(final Blacklist blacklist) {
		addObject(blacklist);
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#isPhoneNumberInBlackList(java.lang.String)
	 */
	public boolean isPhoneNumberInBlackList(final String phoneNumber) {
		var query = getCurrentSession().createQuery("""
		    FROM Blacklist
			WHERE Phone = :Phone
		""", Blacklist.class);
		query.setParameter(Blacklist.PROP_BLA_PHONE, phoneNumber);
		return query.uniqueResult() != null;
	}
	
	public List<Blacklist> getListPhoneNumbersInBlackList() {
        return getCurrentSession().createQuery("""
                FROM Blacklist
            """, Blacklist.class).list();
	}
	//////////////////////////////////////////////////////////////
	// Statistic
	//////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#addStatistic(org.esupportail.smsuapi.dao.beans.Statistic)
	 */
	public void addStatistic(final Statistic statistic) {
		addObject(statistic);
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#isStatisticExistsForApplicationAndAccountAndDate
	 * (org.esupportail.smsuapi.dao.beans.Application, org.esupportail.smsuapi.dao.beans.Account, java.util.Date)
	 */
	public boolean isStatisticExistsForApplicationAndAccountAndDate(final Application application,
							final Account account, final Date date) {
		final Calendar dateAsCal = new GregorianCalendar();
		dateAsCal.setTime(date);
		final int year = dateAsCal.get(Calendar.YEAR);
		// +1 because in calendar Junary is 0, not 1
		final int month = dateAsCal.get(Calendar.MONTH) + 1;
		final int day = dateAsCal.get(Calendar.DAY_OF_MONTH);
		
		final StringBuilder hql = new StringBuilder(200);
		hql.append("select stats from Statistic as stats ");
		hql.append(" where stats.id.App.id = :app_id and ");
		hql.append("       stats.id.Acc.id = :acc_id and ");
		hql.append("       year(stats.id.Month) = :year and");
		hql.append("       month(stats.id.Month) = :month and");
		hql.append("       day(stats.id.Month) = :day");
		
		var query = getCurrentSession().createQuery(hql.toString(), Statistic.class);
		query.setParameter("app_id", application.getId());
		query.setParameter("acc_id", account.getId());
		query.setParameter("year", year);
		query.setParameter("month", month);
		query.setParameter("day", day);
		return query.uniqueResult() != null;
	}


	public record AppAcc(Application app, Account acc) {}
		
	public List<AppAcc> getAppsAndAccounts() {
		var query = getCurrentSession().createQuery("""
		    SELECT distinct new org.esupportail.smsuapi.dao.DaoService$AppAcc(sms.App as App, sms.Acc as Acc) 
			FROM Sms sms
		""", AppAcc.class);
		return query.list(); 
	}


	protected void addObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + object + "...");
		}
		getCurrentSession().persist(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	public void addObjects(final List<?> objects) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + objects + "...");
		}
		for (Object object : objects)
		getCurrentSession().persist(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	/**
	 * Update an object in the database.
	 * @param object
	 */
	protected void updateObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("merging " + object + "...");
		}
		Object merged = getCurrentSession().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, updating " + merged + "...");
		}
	}

}
