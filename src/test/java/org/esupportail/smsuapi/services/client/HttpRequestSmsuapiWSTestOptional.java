package org.esupportail.smsuapi.services.client;

import static org.junit.Assert.*;

import org.esupportail.smsuapi.exceptions.InsufficientQuotaException;
import org.esupportail.smsuapi.exceptions.InvalidParameterException;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.smsuapi.utils.HttpException;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

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
	
	@Test(expected = HttpException.Unreachable.class)  
	public void testBadUrl() throws HttpException, UnknownMessageIdException {
		wsBadUrl().messageInfos(4);
	}
	
	@Test(expected = HttpException.Unreachable.class)  
	public void testWSFirewallDrop() throws HttpException, UnknownMessageIdException {
		wsFirewallDrop().messageInfos(4);
	}
	
	@Test(expected = HttpException.Unreachable.class)  
	public void testBadHost() throws HttpException, UnknownMessageIdException {
		wsBadHost().messageInfos(4);
	}
	
	@Test(expected = HttpException.Unreachable.class)  
	public void testBadHttps() throws HttpException, UnknownMessageIdException {
		wsBadHttps().messageInfos(4);
	}
	
	@Test(expected = SmsuapiWS.AuthenticationFailedException.class)  
	public void testBadUser() throws HttpException, UnknownMessageIdException {
		wsBadUser().messageInfos(4);
	}
	
	@Test(expected = UnknownMessageIdException.class)  
	public void testMessageStatusUnknown() throws HttpException, UnknownMessageIdException {
		wsOk().messageInfos(99999);
	}
	
	@Test
	public void testMessageStatus() throws HttpException, UnknownMessageIdException {
		TrackInfos infos = wsOk().messageInfos(4);
		assertEquals(infos.getNbDestTotal(), (Integer) 1);
	}
	
	@Test(expected = InvalidParameterException.class)  
	public void testSendMissingParam() throws HttpException, InsufficientQuotaException {
		wsOk().sendSms(null, null, "test");
	}
	
	/*
	@Test(expected = InvalidParameterException.class)  
	public void testSendInvalidPhoneNumber() throws HttpException, UnknownMessageIdException {
		wsOk().sendSms(null, "xx", "test");
	}
	*/
	
	@Test(expected = InsufficientQuotaException.class)
	public void testCheckNoQuota() throws HttpException, InsufficientQuotaException {
		wsNoQuota().mayCreateAccountCheckQuotaOk("", 2);
	}
	
	public void testCheckQuota() throws HttpException, InsufficientQuotaException {
		wsOk().mayCreateAccountCheckQuotaOk("", 2);
	}
	
	@Test(expected = InsufficientQuotaException.class)
	public void testSendNoQuota() throws HttpException, InsufficientQuotaException {
		wsNoQuota().sendSms(null, "0601010101", "test");
	}
	
	@Test
	public void testSend() throws HttpException, InsufficientQuotaException {
		Integer id = wsOk().sendSms(null, "0601010101", "test");
		assertNotNull(id);

		Integer id2 = id + 2;
		Integer id2_ = wsOk().sendSms(id2, "0601010101", "test");
		assertEquals(id2, id2_);
	}
	
	public static void all() {
		JUnitCore runner = new JUnitCore();
		runner.addListener(new TextListener(System.out));
		Result result = runner.run(HttpRequestSmsuapiWSTestOptional.class);
		System.out.println("tests finished: " + (result.wasSuccessful() ? "ok" : "KO"));
	}

}