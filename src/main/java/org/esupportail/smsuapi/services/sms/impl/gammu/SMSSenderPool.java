package org.esupportail.smsuapi.services.sms.impl.gammu;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.services.sms.OldISMSSender;


/**
 * SMS sender Pool.
 *
 */
public class SMSSenderPool extends OldISMSSender {

	private final Logger logger = Logger.getLogger(getClass());

	private ConcurrentLinkedQueue<OldISMSSender> pool;

	// 1 sec.
	private final static long POOL_WAIT_AVAILABLE = 1000;


	public void sendMessage(final SMSBroker sms) {
		OldISMSSender smsSender = null;
		try {
			smsSender = borrowSmsSender();
			smsSender.sendMessage(sms);
		} catch (Throwable t) {
			logger.error("An error occurs sending SMS : " + sms);
		} finally {
			returnSmsSender(smsSender);
		}
	}

	private OldISMSSender borrowSmsSender() throws InterruptedException {
		OldISMSSender smsSender;
		while ((smsSender = pool.poll()) == null) {
			Thread.sleep(POOL_WAIT_AVAILABLE);
		}
		return smsSender;
	}

	private void returnSmsSender(OldISMSSender smsSender) {
		if (smsSender == null) {
			return;
		}
		this.pool.offer(smsSender);
	}

	public void setPool(List<OldISMSSender> pool) {
		this.pool = new ConcurrentLinkedQueue<>(pool);
	}

}
