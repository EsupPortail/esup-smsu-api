package org.esupportail.smsuapi.services.servlet;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.junit.Test;

public class RestServletTest {

	@Test
	public void testWriteJson() throws IOException {
		String wanted = "{\"listNumErreur\":[\"0601010101\",\"0623456789\"],\"nbDestBlackList\":3,\"nbDestTotal\":123,\"nbErrorSMS\":2,\"nbProgressSMS\":13,\"nbSentSMS\":100}";
		TrackInfos infos = new TrackInfos();
		infos.setNbDestTotal(123);
		infos.setNbDestBlackList(3);
		infos.setNbSentSMS(100);
		infos.setNbProgressSMS(13);
		infos.setNbErrorSMS(2);
		Set<String> errors = new HashSet<>();
		errors.add("0623456789");
		errors.add("0601010101");
		infos.setListNumErreur(errors);
		
		String json = new ObjectMapper().writeValueAsString(infos);
		assertEquals(wanted, json);
	}

}
