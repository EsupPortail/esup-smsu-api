package org.esupportail.smsuapi.services.servlet;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.esupportail.ws.remote.beans.TrackInfos;
import org.junit.jupiter.api.Test;

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
		Set<String> errors = new LinkedHashSet<>();
		errors.add("0601010101");
		errors.add("0623456789");
		infos.setListNumErreur(errors);
		
		String json = new ObjectMapper().writeValueAsString(infos);
		assertEquals(wanted, json);
	}

}
