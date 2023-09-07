package org.esupportail.smsuapi.services.client;

import static org.junit.jupiter.api.Assertions.*;

import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.junit.jupiter.api.Test;

public class HttpRequestSmsuapiWSTestOptional {

	String urlOk = "http://localhost:8080/";
	String usernameOk = "test-prigaux";
	String passwordOk = "t1t1";

	private HttpRequestSmsuapiWS wsBadHost() {
		return new HttpRequestSmsuapiWS("http://WRONG.WRONG", usernameOk, passwordOk);
	}

	private HttpRequestSmsuapiWS wsFirewallDrop() {
		return new HttpRequestSmsuapiWS("http://google.com:1234", usernameOk, passwordOk);
	}

	private HttpRequestSmsuapiWS wsBadHttps() {
		return new HttpRequestSmsuapiWS("https://localhost:8080/", usernameOk, passwordOk);
	}

	private HttpRequestSmsuapiWS wsBadUrl() {
		return new HttpRequestSmsuapiWS(urlOk + "WRONG", usernameOk, passwordOk);
	}

	private HttpRequestSmsuapiWS wsBadUser() {
		return new HttpRequestSmsuapiWS(urlOk, "badUser", passwordOk);
	}

	private HttpRequestSmsuapiWS wsNoQuota() {
		return new HttpRequestSmsuapiWS(urlOk, "no-quota", "no-quota");
	}

	private HttpRequestSmsuapiWS wsOk() {
		return new HttpRequestSmsuapiWS(urlOk, usernameOk, passwordOk);
	}
	
	@Test
    public void testBadUrl() {
        assertThrows(HttpException.Unreachable.class, () -> wsBadUrl().messageInfos(4));
    }

    @Test
    public void testWSFirewallDrop() {
        assertThrows(HttpException.Unreachable.class, () -> wsFirewallDrop().messageInfos(4));
    }

    @Test
    public void testBadHost() {
        assertThrows(HttpException.Unreachable.class, () -> wsBadHost().messageInfos(4));
    }

    @Test
    public void testBadHttps() {
        assertThrows(HttpException.Unreachable.class, () -> wsBadHttps().messageInfos(4));
    }

    @Test
    public void testBadUser() {
        assertThrows(SmsuapiWS.AuthenticationFailedException.class, () -> wsBadUser().messageInfos(4));
    }

    @Test
    public void testMessageStatusUnknown() {
        assertThrows(UnknownMessageIdException.class, () -> wsOk().messageInfos(99999));
    }

    @Test
    public void testMessageStatus() throws HttpException, UnknownMessageIdException {
        TrackInfos infos = wsOk().messageInfos(4);
        assertEquals((Integer) 1, infos.getNbDestTotal());
    }

    @Test
    public void testSendMissingParam() {
        assertThrows(InvalidParameterException.class, () -> wsOk().sendSms(null, null, "test"));
    }
	
	/*
	@Test(expected = InvalidParameterException.class)  
	public void testSendInvalidPhoneNumber() throws HttpException, UnknownMessageIdException {
		wsOk().sendSms(null, "xx", "test");
	}
	*/
	
    @Test
    public void testCheckNoQuota() {
        assertThrows(InsufficientQuotaException.class, () -> wsNoQuota().mayCreateAccountCheckQuotaOk("", 2));
    }
	
	public void testCheckQuota() throws HttpException, InsufficientQuotaException {
		wsOk().mayCreateAccountCheckQuotaOk("", 2);
	}
	
	@Test
	public void testSendNoQuota() {
	    assertThrows(InsufficientQuotaException.class, () -> wsNoQuota().sendSms(null, "0601010101", "test"));
	}
	
	@Test
	public void testSend() throws HttpException, InsufficientQuotaException {
		Integer id = wsOk().sendSms(null, "0601010101", "test");
		assertNotNull(id);

		Integer id2 = id + 2;
		Integer id2_ = wsOk().sendSms(id2, "0601010101", "test");
		assertEquals(id2, id2_);
	}
}