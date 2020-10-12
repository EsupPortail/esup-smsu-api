package org.esupportail.smsuapi.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/properties/applicationContext-test.xml")
public class SmsuApiTestDerbySetup extends AbstractJUnit4SpringContextTests {

    protected static final Logger logger = Logger.getLogger(SmsuApiTestDerbySetup.class);

    @Named("jdbcSessionFactory")
    @Inject protected SessionFactory jdbcSessionFactory;

	@Inject 
	private DaoService daoService;
    
    protected FlushMode flushMode = FlushMode.AUTO;
    
	static final String ACCOUNT_NAME = "esup-smsu-api-account";
	static final String APP_NAME = "esup-smsu-api-test-app";
    
    @BeforeClass
    public static void initDerbyDb() throws Exception {
    	Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection connection = DriverManager.getConnection("jdbc:derby:memory:smsuapi;user=smsuapi;password=esup;create=true");
        for(String sqlFile : new String[]{"database/test-derby-create_tables.sql", "database/test-derby-populate_tables.sql"}) {
	        ClassPathResource sqlCreateTablesRes = new ClassPathResource(sqlFile);
	        for(String sql : (List<String>)IOUtils.readLines(sqlCreateTablesRes.getInputStream())) {
	        	sql = sql.replaceFirst(";$", "");
	        	logger.info(sql);
	        	connection.createStatement().execute(sql);
	        }
        };
        logger.info("Database Test OK");
        Statement s = connection.createStatement();
        s.execute("select * from application");
        logger.info(s.getResultSet());
    }

    @Before
    public void setUp() throws Exception {
        Session session = getSession();
        TransactionSynchronizationManager.bindResource(jdbcSessionFactory,new SessionHolder(session));
        logger.debug("Hibernate session is bound");
    }

	@After
    public void tearDown() throws Exception {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(jdbcSessionFactory);
        closeSession(sessionHolder.getSession(), jdbcSessionFactory);
        logger.debug("Hibernate session is closed");
    }

    protected void closeSession(Session session, SessionFactory sessionFactory) {
        SessionFactoryUtils.closeSession(session);
    }

    protected Session getSession() throws DataAccessResourceFailureException {
        Session session = SessionFactoryUtils.getSession(jdbcSessionFactory, true);
        FlushMode flushMode = this.flushMode;
        if (flushMode != null) {
            session.setFlushMode(flushMode);
        }
        return session;
    }

    protected Account getAccount() {
    	return daoService.getAccByLabel(ACCOUNT_NAME);
    }
    
    protected Application getApplication() {
    	Application app = daoService.getApplicationByName(APP_NAME);
    	logger.info(String.format("Application %s", app));
    	return app;
    }
}


