package org.esupportail.smsuapi.services.sms.impl.gammu;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.domain.beans.sms.SMSBroker;
import org.esupportail.smsuapi.services.sms.ISMSSender;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;


/**
 * Gammu SMS sender.
 *
 */
public class SMSSenderGammu implements ISMSSender {

	private final Logger logger = new LoggerImpl(getClass());


	private boolean simulateMessageSending;

	private String gammuConfigFileFullPath;

	private String pinCode;

	public synchronized void sendMessage(final SMSBroker sms) {

		final int smsId = sms.getId();
		final String smsRecipient = sms.getRecipient();
		final String smsMessage = sms.getMessage();

		if (logger.isDebugEnabled()) {
			logger.debug("Entering into send message with parameter : ");
			logger.debug("   - message id : " + smsId);
			logger.debug("   - message recipient : " + smsRecipient);
			logger.debug("   - message : " + smsMessage);
		}

		try {
			final String messageISOLatin = new String(smsMessage.getBytes(),"ISO-8859-1");
			if (logger.isDebugEnabled()) {
				logger.debug("sending encoded message : " + messageISOLatin);
			}

			// only send the message if required
			if (!simulateMessageSending) {

				String cmdPinCodeReturn = execCmd(new String[] { "gammu", "-c", gammuConfigFileFullPath, "entersecuritycode", "PIN", pinCode}, null);
				logger.info(cmdPinCodeReturn);

				String cmdSensSmsReturn = execCmd(new String[] {"gammu", "-c", gammuConfigFileFullPath, "--sendsms", "text", smsRecipient}, smsMessage);
				logger.info(cmdSensSmsReturn);

				if (logger.isDebugEnabled()) {
					logger.debug("message with : " + 
							" - id : " + smsId + "successfully sent");
				}
			} else {
				logger.warn("Message with id : " + smsId + " not sent because simlation mode is enable");
			}


		} catch (Throwable t) {
			logger.error("An error occurs sending SMS : " + 
					" - id : " + smsId + 
					" - recipient : " + smsRecipient + 
					" - message : " + smsMessage);
		}		

	}

	private String execCmd(String[] cmd, String input) throws java.io.IOException {
		Process process = Runtime.getRuntime().exec(cmd);
		if(input != null) {
			IOUtils.copy(IOUtils.toInputStream(input), process.getOutputStream());
			// we have to close the outputStream here 
			process.getOutputStream().close();
		}

		// err output
		String errLog = IOUtils.toString(process.getErrorStream());
		if(!errLog.isEmpty()) {
			logger.debug(IOUtils.toString(process.getErrorStream()));
		}

		// std output
		return IOUtils.toString(process.getInputStream());
	}


	@Required
	public void setSimulateMessageSending(final boolean simulateMessageSending) {
		this.simulateMessageSending = simulateMessageSending;
	}

	@Required
	public void setGammuConfigFileCpRessource(String gammuConfigFileCpRessource) throws IOException {
		ClassPathResource gammuConfRessource = new ClassPathResource(gammuConfigFileCpRessource);
		this.gammuConfigFileFullPath = gammuConfRessource.getFile().getAbsolutePath();
	}

	@Required
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

}
