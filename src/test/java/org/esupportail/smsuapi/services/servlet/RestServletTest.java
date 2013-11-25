package org.esupportail.smsuapi.services.servlet;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.esupportail.ws.remote.beans.TrackInfos;
import org.junit.Test;

import com.google.gson.Gson;

public class RestServletTest {

	@Test
	public void testWriteJson() {
		String wanted = "{\"nbDestTotal\":123,\"nbDestBlackList\":3,\"nbSentSMS\":100,\"nbProgressSMS\":13,\"nbErrorSMS\":2,\"listNumErreur\":[\"0601010101\",\"0623456789\"]}";
		TrackInfos infos = new TrackInfos();
		infos.setNbDestTotal(123);
		infos.setNbDestBlackList(3);
		infos.setNbSentSMS(100);
		infos.setNbProgressSMS(13);
		infos.setNbErrorSMS(2);
		Set<String> errors = new HashSet<String>();
		errors.add("0623456789");
		errors.add("0601010101");
		infos.setListNumErreur(errors);
		
		String json = new Gson().toJson(infos);
		assertEquals(wanted, json);
	}

}
