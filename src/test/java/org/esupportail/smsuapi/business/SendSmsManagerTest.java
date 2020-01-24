package org.esupportail.smsuapi.business;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Account;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.junit.Test;

public class SendSmsManagerTest extends SmsuApiTestDerbySetup {

	final Logger logger = Logger.getLogger(getClass());
			
	@Inject 
	private DaoService daoService;

	@Inject 
	private SendSmsManager smsManager;

	@Test
	public void sendSMSTest() throws InsufficientQuotaException {

		int msg_id = 1;
		int nb_msg = 10;

		Application app = getApplication();
		Account acc = getAccount(); 

		String[] phoneNumbers = getPhoneNumbers(nb_msg);
		
		long time = System.currentTimeMillis();
		logger.info(String.format("%s sms will be sent ...", nb_msg));
		smsManager.sendSMS(msg_id, acc.getId(), phoneNumbers, acc.getLabel(), "test", app);
		logger.info(String.format("%s sms sent call take : %s sec.", nb_msg, (long)(System.currentTimeMillis()-time)/1000));
		
		int j = daoService.getNbProgressSMS(msg_id, app);

		assertEquals(nb_msg, j);
	}

	private String[] getPhoneNumbers(int nb_msg) {
		String[] phoneNumbers = new String[nb_msg];
		for (int i = 0 ; i < nb_msg ; i++) {
			phoneNumbers[i] = Integer.toString(1000000000 + i);
		}
		return phoneNumbers;
	}
}
