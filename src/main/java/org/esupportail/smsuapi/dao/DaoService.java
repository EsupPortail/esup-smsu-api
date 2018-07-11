/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.dao;



import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Blacklist;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.dao.beans.Statistic;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;


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
	@SuppressWarnings("unchecked")
	public Account getAccByLabel(final String labelAccount) {
		Criteria criteria = getCurrentSession().createCriteria(Account.class);
		criteria.add(Restrictions.eq(Account.PROP_LABEL, labelAccount));
		List<Account> acc = criteria.list(); 
		if (acc.size() != 0) { return acc.get(0);
		} else { return null; }
		
	}
	

	/**
	 * used by sendSMS.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	@SuppressWarnings("unchecked")
	public int getBlackLListByPhone(final String phone) {
		Criteria criteria = getCurrentSession().createCriteria(Blacklist.class);
		criteria.add(Restrictions.eq(Blacklist.PROP_BLA_PHONE, phone));
		List<Blacklist> bla = criteria.list();  
		return  bla.size();  
		
	}

	//////////////////////////////////////////////////////////////
	// WS SendTrack methods
	//////////////////////////////////////////////////////////////

	/**
	 * @return the number of SMS recipients.
	 */
	@SuppressWarnings("unchecked")
	public int getNbDest(final Integer msgId, final Application app) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			List<Sms> sms = criteria.list();  
			return  sms.size();   
	}
	
	/**
	 * @return the number of sms having one of the wanted states
	 */
	@SuppressWarnings("unchecked")
	public int getNbSmsWithState(final Integer msgId, final Application app, final List<String> list) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			List<Sms> sms = criteria.list();  
			return  sms.size();   
			
	}
	
	/**
	 * @return the number of sent SMS.
	 */
	@SuppressWarnings("unchecked")
	public int getNbSentSMS(final Integer msgId, final Application app) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.eq(Sms.PROP_STATE, SmsStatus.DELIVERED.name()));
			List<Sms> sms = criteria.list();  
				if (sms.isEmpty()) { return 0; 
				} else { return  sms.size(); }  
			

	}
	
	/**
	 * @return the number of SMS in progress.
	 */
	@SuppressWarnings("unchecked")
	public int getNbProgressSMS(final Integer msgId, final Application app) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.eq(Sms.PROP_STATE, SmsStatus.IN_PROGRESS.name()));
			List<Sms> sms = criteria.list();  
				if (sms.isEmpty()) { return 0; 
				} else { return  sms.size(); }  
			

	}
	
	/**
	 * @return the number of SMS in error.
	 */
	@SuppressWarnings("unchecked")
	public int getNbErrorSMS(final Integer msgId, final Application app, final List<String> list) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			List<Sms> sms = criteria.list();  
			return  sms.size();   
			
	}
	
	/**
	 * @return the list of phones SMS in error.
	 */
	@SuppressWarnings("unchecked")
	public List<Sms> getListNumErreur(final Integer msgId, final Application app, final List<String> list) {
			Criteria criteria = getCurrentSession().createCriteria(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			return criteria.list();  
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
		Criteria criteria = currentSession.createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_BROKER_SMS_ID, id));
		return (Sms) criteria.uniqueResult();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esupportail.smsuapi.dao.DaoService#getSms(org.esupportail.smsuapi.dao.beans.Application, int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Sms> getSms(final Application app, final  int id, final String phoneNumber) {
		Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID,id));
		criteria.add(Restrictions.eq(Sms.PROP_APP,app));
		criteria.add(Restrictions.eq(Sms.PROP_PHONE, phoneNumber));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Sms> getSms(SmsStatus status) {
		Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_STATE, status.name()));
		return criteria.list();		
	}

	public Integer getNewInitialId(Application app) {
		Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		criteria.setProjection( Projections.max(Sms.PROP_INITIAL_ID) )
				.add(Restrictions.eq(Sms.PROP_APP, app));

		Integer max = (Integer) criteria.uniqueResult();
		
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
		final Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_APP, application));
		criteria.add(Restrictions.eq(Sms.PROP_ACC, account));
		
		criteria.setProjection(Projections.min(Sms.PROP_DATE));
		return (Date) criteria.uniqueResult();
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getNbOfSmsByAppAndAccountAndDate
	 * (org.esupportail.smsuapi.dao.beans.Application, 
	 * org.esupportail.smsuapi.dao.beans.Account, java.util.Date, java.util.Date)
	 */
	public int getNbOfSmsByAppAndAccountAndDate(final Application application, final Account account,
						final Date startDate, final Date endDate) {
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_APP, application));
		criteria.add(Restrictions.eq(Sms.PROP_ACC, account));
		criteria.add(Restrictions.between(Sms.PROP_DATE, startDate, endDate));
		
		criteria.setProjection(Projections.rowCount());
		final Long count = (Long) criteria.uniqueResult();
		return count.intValue();
	}
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getNbOfSmsInErrorByAppAndAccountAndDate
	 * (org.esupportail.smsuapi.dao.beans.Application, org.esupportail.smsuapi.dao.beans.Account, 
	 * java.util.Date, java.util.Date)
	 */
	public int getNbOfSmsInErrorByAppAndAccountAndDate(final Application application, final Account account,
						final Date startDate, final Date endDate) {
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_APP, application));
		criteria.add(Restrictions.eq(Sms.PROP_ACC, account));
		criteria.add(Restrictions.between(Sms.PROP_DATE, startDate, endDate));
		criteria.add(Restrictions.or(
				          Restrictions.eq(Sms.PROP_STATE, SmsStatus.ERROR.name()), 
				          Restrictions.or(
		        		  Restrictions.eq(Sms.PROP_STATE, SmsStatus.ERROR_POST_BL.name()),
		        		  Restrictions.eq(Sms.PROP_STATE, SmsStatus.ERROR_PRE_BL.name())
				          )
				    ));      
				          
		criteria.setProjection(Projections.rowCount());
		final Long count = (Long) criteria.uniqueResult();
		return count.intValue();
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#deleteSmsOlderThan(java.util.Date)
	 */
	public int deleteSmsOlderThan(final Date date) {
		final String hql = "delete from Sms as sms where sms.Date < :date";
		
		final Query query = getCurrentSession().createQuery(hql);
		query.setTimestamp("date", date);
		
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
		Criteria criteria = getCurrentSession().createCriteria(Application.class);
		criteria.add(Restrictions.eq(Application.PROP_NAME, name));
		return (Application) criteria.uniqueResult();
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
		final Criteria criteria = getCurrentSession().createCriteria(Blacklist.class);
		criteria.add(Restrictions.eq(Blacklist.PROP_BLA_PHONE, phoneNumber));
		return criteria.uniqueResult() != null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Blacklist> getListPhoneNumbersInBlackList() {
        return getCurrentSession().createCriteria(Blacklist.class).list();
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
		
		final Query query = getCurrentSession().createQuery(hql.toString());
		query.setInteger("app_id", application.getId());
		query.setInteger("acc_id", account.getId());
		query.setInteger("year", year);
		query.setInteger("month", month);
		query.setInteger("day", day);
		return query.uniqueResult() != null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,?>> getAppsAndAccounts() {
		Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		
		criteria.setProjection(Projections.projectionList()
				.add( Projections.distinct(Projections.projectionList()
						.add(Projections.property(Sms.PROP_APP), Sms.PROP_APP)
						.add(Projections.property(Sms.PROP_ACC), Sms.PROP_ACC))));
		
		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		
		List<Map<String,?>> result = criteria.list(); 
		
		return result;
	}


	protected void addObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + object + "...");
		}
		getCurrentSession().beginTransaction();
		getCurrentSession().save(object);
		getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	public void addObjects(final List<?> objects) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + objects + "...");
		}
		getCurrentSession().beginTransaction();
		for (Object object : objects)
		getCurrentSession().save(object);
		getCurrentSession().getTransaction().commit();
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
		getCurrentSession().beginTransaction();
		Object merged = getCurrentSession().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, updating " + merged + "...");
		}
		getCurrentSession().update(merged);
		getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

}
