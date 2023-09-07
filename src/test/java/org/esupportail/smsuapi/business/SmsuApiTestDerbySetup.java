package org.esupportail.smsuapi.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringJUnitConfig(locations = "classpath:/properties/applicationContext-test.xml")
@TestInstance(Lifecycle.PER_CLASS)
public class SmsuApiTestDerbySetup {

    protected static final Logger logger = Logger.getLogger(SmsuApiTestDerbySetup.class);

    @Inject protected SessionFactory jdbcSessionFactory;

	@Inject 
	private DaoService daoService;
    
    protected FlushMode flushMode = FlushMode.AUTO;
    
	static final String ACCOUNT_NAME = "esup-smsu-api-account";
	static final String APP_NAME = "esup-smsu-api-test-app";
    
    @BeforeAll
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

    @BeforeEach
    public void setUp() throws Exception {
        Session session = getSession();
        TransactionSynchronizationManager.bindResource(jdbcSessionFactory, new SessionHolder(session));
        logger.debug("Hibernate session is bound");
    }

	@AfterEach
    public void tearDown() throws Exception {
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(jdbcSessionFactory);
        closeSession(sessionHolder.getSession(), jdbcSessionFactory);
        logger.debug("Hibernate session is closed");
    }

    protected void closeSession(Session session, SessionFactory sessionFactory) {
        session.close();
    }

    protected Session getSession() throws DataAccessResourceFailureException {
        TransactionSynchronizationManager.hasResource(jdbcSessionFactory);
        Session session = jdbcSessionFactory.openSession();
        FlushMode flushMode = this.flushMode;
        if (flushMode != null) {
            session.setHibernateFlushMode(flushMode);
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


