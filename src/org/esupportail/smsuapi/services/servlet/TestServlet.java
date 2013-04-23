package org.esupportail.smsuapi.services.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esupportail.commons.services.database.DatabaseUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.BeanUtils;
import org.esupportail.smsuapi.business.context.ApplicationContextUtils;
import org.esupportail.smsuapi.business.purge.PurgeSms;
import org.esupportail.smsuapi.business.purge.PurgeStatistic;
import org.esupportail.smsuapi.business.stats.StatisticBuilder;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.domain.DomainService;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.services.scheduler.SchedulerUtils;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.esupportail.smsuapi.services.sms.ackmanagement.AckManager;
import org.esupportail.smsuapi.services.sms.ackmanagement.Acknowledgment;

/**
 * Servlet used only during test.
 * @author PRQD8824
 *
 */
public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -518464944485395827L;

	  /**
     * logger
     */
	private final Logger logger = new LoggerImpl(getClass());
	

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest req, 
    					 final HttpServletResponse resp) throws ServletException,
        										   			  	IOException {
        execute(req, resp);
    }
    
    private void execute(final HttpServletRequest req, 
    					 final HttpServletResponse resp) {
    	
    	
    	final String testId = req.getParameter("testId");
    	
    	if ("sms".equalsIgnoreCase(testId)) {
    		testSms();
    	} else if ("smsack".equalsIgnoreCase(testId)) {
    		testSmsAck();
    	} else if ("stat".equalsIgnoreCase(testId)) {
    		testStat();
    	} else if ("qrtz".equalsIgnoreCase(testId)) {
    		testQrtz();
    	} else if ("dao".equalsIgnoreCase(testId)) {
    		testDao();
    	} else if ("numerr".equalsIgnoreCase(testId)) {
    		testNumErr();
    	} else if ("purgeSms".equalsIgnoreCase(testId)) {
    		testPurgeSms();
    	} else if ("purgeStats".equalsIgnoreCase(testId)) {
    		testPurgeStatistic();
    	}
    }
    
   
    
    private void testSms() {
    	ISMSSender smsSender = (ISMSSender) BeanUtils.getBean("smsSenderImpl");
    	final SMSBroker smsMessage = new SMSBroker(1, "0699999999", "coucou", "");
    	smsSender.sendMessage(smsMessage);
    }
    
    private void testSmsAck() {
    	AckManager ackManager = (AckManager) BeanUtils.getBean("ackManager");
    	Acknowledgment ack = new Acknowledgment();
    	ack.setSmsId(8);
    	ack.setSmsStatus(SmsStatus.ERROR_POST_BL);
    	
    	ackManager.manageAck(ack);

    }
    
    private void testStat() {
    	ApplicationContextUtils.initApplicationContext();
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DaoService daoService = (DaoService) BeanUtils.getBean("daoService");
    		@SuppressWarnings("unused")
			final Application app = daoService.getApplicationByName("application name");
    		@SuppressWarnings("unused")
			final Account acc = daoService.getAccByLabel("account label");
    		
    		@SuppressWarnings("unused")
			final Calendar begin = new GregorianCalendar(2009, 6, 11);

    		StatisticBuilder statisticBuilder = (StatisticBuilder) BeanUtils.getBean("statisticBuilder");
    		statisticBuilder.buildAllStatistics();
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    	} finally {
    		DatabaseUtils.close();
    	}
    	
    }
    
    private void testQrtz() {
    	ApplicationContextUtils.initApplicationContext();
    	SchedulerUtils schedulerUtils = (SchedulerUtils) BeanUtils.getBean("schedulerUtils");
    	SMSBroker smsb = new SMSBroker(0, "Un message", "0232323232", "");
    	
    	List<SMSBroker> listSms = new LinkedList<SMSBroker>();
    	listSms.add(smsb);
    	schedulerUtils.launchSuperviseSmsSending(listSms);
    }
    
    @SuppressWarnings("deprecation")
	private void testDao() {
    	ApplicationContextUtils.initApplicationContext();
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DaoService daoService = (DaoService) BeanUtils.getBean("daoService");
    		final Application app = daoService.getApplicationByName("application name");
    		final Account acc = daoService.getAccByLabel("account label");
    		final Date theDate = new Date(109,6,1);
    		@SuppressWarnings("unused")
			final boolean bool = daoService.isStatisticExistsForApplicationAndAccountAndDate(app, acc, theDate);
    		
    		
    		
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		DatabaseUtils.rollback();
    	} finally {
    		DatabaseUtils.close();
    	}
    }
    
     private void testNumErr() {
    	
    	try {
    		DatabaseUtils.open();
    		DatabaseUtils.begin();
    		
    		DomainService domainService = (DomainService) BeanUtils.getBean("domainService");
    		//logger.debug("ici : 0666666666");
    		//Boolean retVal = domainService.isPhoneNumberInBlackList("0665178942");
    		//logger.debug("ici reponse pour 0666666666 : " + retVal);
    		@SuppressWarnings("unused")
			Set<String> list = domainService.getListPhoneNumbersInBlackList();
    	
    		DatabaseUtils.commit();
    	} catch (Throwable t) {
    		logger.debug(t.getMessage());
    		DatabaseUtils.rollback();
    	} finally {
    		DatabaseUtils.close();
    	}
    }
     
     private void testPurgeSms() {
    	 
    	 ApplicationContextUtils.initApplicationContext();
    		 
    		 try {
    			 DatabaseUtils.open();
    			 DatabaseUtils.begin();

    			 final PurgeSms purgeSms = (PurgeSms) BeanUtils.getBean("purgeSms");
    			 purgeSms.purgeSms();

    			 DatabaseUtils.commit();
    		 } catch (Throwable t) {
    			 logger.debug(t.getMessage());
    			 DatabaseUtils.rollback();
    		 } finally {
    			 DatabaseUtils.close();
    		 }
     }
     
     private void testPurgeStatistic() {
    	 
    	 ApplicationContextUtils.initApplicationContext();
    		 
    		 try {
    			 DatabaseUtils.open();
    			 DatabaseUtils.begin();

    			 final PurgeStatistic purgeStatistic = (PurgeStatistic) BeanUtils.getBean("purgeStatistic");
    			 purgeStatistic.purgeStatistic();

    			 DatabaseUtils.commit();
    		 } catch (Throwable t) {
    			 logger.debug(t.getMessage());
    			 DatabaseUtils.rollback();
    		 } finally {
    			 DatabaseUtils.close();
    		 }
     }
}
