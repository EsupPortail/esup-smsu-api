package org.esupportail.smsuapi.services.sms.impl.olm;

import org.springframework.beans.factory.InitializingBean;

import fr.cvf.util.mgs.Connector;
import fr.cvf.util.mgs.ConnectorFactory;
import fr.cvf.util.mgs.ErrorManager;
import fr.cvf.util.mgs.mode.sgs.message.request.SMText;

/**
 * Use to wrap olm connector.
 * @author PRQD8824
 *
 */
public class OlmConnector implements InitializingBean {

	/**
	 * The Olm connector.
	 */
	private Connector connector;

	/**
	 * The connector name (the named used in connector property file).
	 */
	private String connectorName;
	
	/**
	 * The olm connector property file.
	 */
	private String olmConnectorPropertyFile;
	
	/**
	 * The olm ack manger impl.
	 */
	private OlmAckManager olmAckManger;
	
	/**
	 * The olm error and warning manager.
	 */
	private ErrorManager errorManager;
	
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		connector = ConnectorFactory.create(olmConnectorPropertyFile, connectorName);
		connector.setResponseManager(olmAckManger);
		connector.setErrorManager(errorManager);
		connector.configure();
	}
	
	/**
	 * Use to send SMText (text sms).
	 * @param smText
	 */
	public void submit(final SMText smText) {
		connector.submit(smText);
	}
	
	
	/***********
	 * Mutator
	 */
	

	/**
	 * Standard setter used by Spring.
	 * @param connectorName
	 */
	public void setConnectorName(final String connectorName) {
		this.connectorName = connectorName;
	}


	/**
	 * Standard setter used by Spring.
	 * @param olmConnectorPropertyFile
	 */
	public void setOlmConnectorPropertyFile(final String olmConnectorPropertyFile) {
		this.olmConnectorPropertyFile = olmConnectorPropertyFile;
	}
	
	/**
	 * Standard setter used by Spring.
	 * @param olmAckManger
	 */
	public void setOlmAckManger(final OlmAckManager olmAckManger) {
		this.olmAckManger = olmAckManger;
	}
	
	/**
	 * Standard setter used by spring.
	 * @param errorManager
	 */
	public void setErrorManager(final ErrorManager errorManager) {
		this.errorManager = errorManager;
	}
}
