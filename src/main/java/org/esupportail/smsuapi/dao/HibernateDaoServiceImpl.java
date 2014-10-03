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
import org.hibernate.classic.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * The Hibernate implementation of the DAO service.
 */
public class HibernateDaoServiceImpl extends HibernateDaoSupport
			 implements DaoService, InitializingBean {

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 3152554337896617315L;
	
	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Bean constructor.
	 */
	public HibernateDaoServiceImpl() {
		super();
	}

	/**
	 * retrieve the current session.
	 * @return
	 */
	private Session getCurrentSession() {
		return getHibernateTemplate().getSessionFactory().getCurrentSession();
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
		DetachedCriteria criteria = DetachedCriteria.forClass(Account.class);
		criteria.add(Restrictions.eq(Account.PROP_LABEL, labelAccount));
		List<Account> acc = getHibernateTemplate().findByCriteria(criteria); 
		if (acc.size() != 0) { return acc.get(0);
		} else { return null; }
		
	}
	

	/**
	 * used by sendSMS.
	 * @see org.esupportail.smsuapi.services.remote.SendSms#getQuota()
	 */
	@SuppressWarnings("unchecked")
	public int getBlackLListByPhone(final String phone) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Blacklist.class);
		criteria.add(Restrictions.eq(Blacklist.PROP_BLA_PHONE, phone));
		List<Blacklist> bla = getHibernateTemplate().findByCriteria(criteria);  
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
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
			return  sms.size();   
	}
	
	/**
	 * @return the number of sms having one of the wanted states
	 */
	@SuppressWarnings("unchecked")
	public int getNbSmsWithState(final Integer msgId, final Application app, final List<String> list) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
			return  sms.size();   
			
	}
	
	/**
	 * @return the number of sent SMS.
	 */
	@SuppressWarnings("unchecked")
	public int getNbSentSMS(final Integer msgId, final Application app) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.eq(Sms.PROP_STATE, SmsStatus.DELIVERED.name()));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
				if (sms.isEmpty()) { return 0; 
				} else { return  sms.size(); }  
			

	}
	
	/**
	 * @return the number of SMS in progress.
	 */
	@SuppressWarnings("unchecked")
	public int getNbProgressSMS(final Integer msgId, final Application app) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.eq(Sms.PROP_STATE, SmsStatus.IN_PROGRESS.name()));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
				if (sms.isEmpty()) { return 0; 
				} else { return  sms.size(); }  
			

	}
	
	/**
	 * @return the number of SMS in error.
	 */
	@SuppressWarnings("unchecked")
	public int getNbErrorSMS(final Integer msgId, final Application app, final List<String> list) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
			return  sms.size();   
			
	}
	
	/**
	 * @return the list of phones SMS in error.
	 */
	@SuppressWarnings("unchecked")
	public List<Sms> getListNumErreur(final Integer msgId, final Application app, final List<String> list) {
			DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
			criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID, msgId));
			criteria.add(Restrictions.eq(Sms.PROP_APP, app));
			criteria.add(Restrictions.in(Sms.PROP_STATE, list));
			List<Sms> sms = getHibernateTemplate().findByCriteria(criteria);  
			return  sms;   
			
	}

	//////////////////////////////////////////////////////////////
	// SMS
	//////////////////////////////////////////////////////////////
	
	/**
	 * @param id
	 * @return
	 */
	public Sms getSms(final int id) {
		return (Sms) getHibernateTemplate().get(Sms.class, id);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esupportail.smsuapi.dao.DaoService#getSms(org.esupportail.smsuapi.dao.beans.Application, int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Sms> getSms(final Application app, final  int id, final String phoneNumber) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_INITIAL_ID,id));
		criteria.add(Restrictions.eq(Sms.PROP_APP,app));
		criteria.add(Restrictions.eq(Sms.PROP_PHONE, phoneNumber));
		List<Sms> lstSms = getHibernateTemplate().findByCriteria(criteria);
		
		return lstSms;
	}
	
	@SuppressWarnings("unchecked")
	public List<Sms> getSms(SmsStatus status) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_STATE, status.name()));
		List<Sms> lstSms = getHibernateTemplate().findByCriteria(criteria);		
		return lstSms;
	}

	public List<Sms> getSmss() {
		return getHibernateTemplate().loadAll(Sms.class);
	}

	/**
	 * 
	 * @param sms
	 */
	public synchronized void addSms(final Sms sms) {
		if (sms.getInitialId() == null) {
			sms.setInitialId(getNewInitialId(sms.getApp()));
		}
		addObject(sms);
	}

	
	private Integer getNewInitialId(Application app) {
		Criteria criteria = getCurrentSession().createCriteria(Sms.class);
		criteria.setProjection( Projections.max(Sms.PROP_INITIAL_ID) )
				.add(Restrictions.eq(Sms.PROP_APP, app));

		Integer max = (Integer) criteria.uniqueResult();
		
		return 1 + (max != null ? max : 0);
	}

	/**
	 * @param sms
	 */
	public void deleteSms(final Sms sms) {
		deleteObject(sms);
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
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Sms.class);
		criteria.add(Restrictions.eq(Sms.PROP_APP, application));
		criteria.add(Restrictions.eq(Sms.PROP_ACC, account));
		
		criteria.setProjection(Projections.min(Sms.PROP_DATE));
		final Date result = (Date) criteria.uniqueResult();
		return result;
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
		final Integer count = (Integer) criteria.uniqueResult();
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
		final Integer count = (Integer) criteria.uniqueResult();
		return count.intValue();
	}
	
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#deleteSmsOlderThan(java.util.Date)
	 */
	public int deleteSmsOlderThan(final Date date) {
		final Session currentSession = getCurrentSession();
		
		final StringBuilder hql = new StringBuilder(200);
		hql.append("delete from Sms as sms where  sms.Date < :date");
		
		final Query query = currentSession.createQuery(hql.toString());
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
	
	/* (non-Javadoc)
	 * @see org.esupportail.smsuapi.dao.DaoService#getAllApplications()
	 */
	@SuppressWarnings("unchecked")
	public List<Application> getAllApplications() {
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Application.class);
		final List<Application> allApplicationList = criteria.list();
		return allApplicationList;
	}
 	
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
		Session currentSession = getCurrentSession();
		Criteria criteria = currentSession.createCriteria(Application.class);
		criteria.add(Restrictions.eq(Application.PROP_NAME, name));
		Application application = (Application) criteria.uniqueResult();
		return application;
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
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Blacklist.class);
		criteria.add(Restrictions.eq(Blacklist.PROP_BLA_PHONE, phoneNumber));
		final Blacklist phoneNumberInBlacklist =  (Blacklist) criteria.uniqueResult();
		
		boolean retVal;
		if (phoneNumberInBlacklist != null) {
			retVal = true;
		} else {
			retVal = false;
		}
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public List<Blacklist> getListPhoneNumbersInBlackList() {
		final Session currentSession = getCurrentSession();
		final Criteria criteria = currentSession.createCriteria(Blacklist.class);
		
		final List<Blacklist> listPhoneNumbersInBlackList =  criteria.list();
		
		return listPhoneNumbersInBlackList;
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
		final Session currentSession = getCurrentSession();
		
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
		
		final Query query = currentSession.createQuery(hql.toString());
		query.setInteger("app_id", application.getId());
		query.setInteger("acc_id", account.getId());
		query.setInteger("year", year);
		query.setInteger("month", month);
		query.setInteger("day", day);
		
		final Statistic result = (Statistic) query.uniqueResult();
		
		boolean exist;
		if (result != null) {
			exist = true;
		} else {
			exist = false;
		}

		return exist;
	}
	
	/**
	 * Return Statistic.
	 * @param month
	 * @param year
	 * @return
	 */
	public Statistic getStatisticByApplicationAndMonthAndYear(final Application application, 
			final int month, final int year) {
		final Session currentSession = getCurrentSession();
		
		final StringBuilder hql = new StringBuilder(200);
		hql.append("select stats from Statistic as stats ");
		hql.append(" where stats.id.App.id = :app_id and ");
		hql.append("       year(stats.id.Month) = :year and");
		hql.append("       month(stats.id.Month) = :month");
		
		final Query query = currentSession.createQuery(hql.toString());
		query.setInteger("app_id", application.getId());
		query.setInteger("year", year);
		query.setInteger("month", month);
		
		final Statistic result = (Statistic) query.uniqueResult();
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public List<Map<String,?>> getAppsAndCountsToTreat() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Sms.class);
		
		criteria.setProjection(Projections.projectionList()
				.add( Projections.distinct(Projections.projectionList()
						.add(Projections.property(Sms.PROP_APP), Sms.PROP_APP)
						.add(Projections.property(Sms.PROP_ACC), Sms.PROP_ACC))));
		
		criteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		
		List<Map<String,?>> result = getHibernateTemplate().findByCriteria(criteria); 
		
		return result;
	}


	protected void addObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("adding " + object + "...");
		}
		getCurrentSession().beginTransaction();
		getHibernateTemplate().save(object);
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
		Object merged = getHibernateTemplate().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, updating " + merged + "...");
		}
		getHibernateTemplate().update(merged);
		getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

	/**
	 * Delete an object from the database.
	 * @param object
	 */
	protected void deleteObject(final Object object) {
		if (logger.isDebugEnabled()) {
			logger.debug("merging " + object + "...");
		}
		getCurrentSession().beginTransaction();
		Object merged = getHibernateTemplate().merge(object);
		if (logger.isDebugEnabled()) {
			logger.debug("done, deleting " + merged + "...");
		}
		getHibernateTemplate().delete(merged);
                getCurrentSession().getTransaction().commit();
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
	}

}
