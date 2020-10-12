package org.esupportail.smsuapi.services.sms;


/**
 * Interface of all SMS Sender implementation.
 * @author PRQD8824
 *
 */
public abstract class OldISMSSender implements ISMSSender {
    static public class SMSBroker {

        /**
         * The unique identifier message.
         */
        private int id;
        
        /**
         * The message recipient.
         */
        private String recipient;
        
        /**
         * The message itself.
         */
        private String message;
        
        private String accountLabel;

        public SMSBroker(final int id, final String recipient, final String message, final String accountLabel) {
            super();
            this.id = id;
            this.recipient = recipient;
            this.message  = message;
            this.accountLabel = accountLabel;
        }

        public int getId() {
            return id;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getMessage() {
            return message;
        }

        public String getAccountLabel() {
            return accountLabel;
        }
        
    }

	/**
	 * Send the specified message.
	 * @param smsMessage
	 */
	public abstract void sendMessage(SMSBroker smsMessage);

    public void sendMessage(org.esupportail.smsuapi.domain.beans.sms.SMSBroker smsMessageList, String force_login, String force_password) {
        if (force_login != null || force_password != null) throw new RuntimeException("broker does not handle forced login/password");
        for (org.esupportail.smsuapi.domain.beans.sms.SMSBroker.Rcpt rcpt : smsMessageList.rcpts) {
            sendMessage(new SMSBroker(rcpt.id, rcpt.recipient, smsMessageList.message, smsMessageList.accountLabel));
        }
    }
}
