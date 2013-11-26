package org.esupportail.smsuapi.services.sms.impl.smsenvoi;

import static org.junit.Assert.*;

import org.codehaus.jackson.JsonNode;
import org.esupportail.smsuapi.services.sms.impl.smsenvoi.AckStatusSmsenvoi;
import org.esupportail.smsuapi.services.sms.impl.smsenvoi.RequestSmsenvoi;
import org.junit.Test;

public class AckStatusSmsenvoiTest {

	static final String checkdeliveryError_ar2 = "{\"success\":1,\"listing\":{\"999\":{\"id\":\"xxx\",\"recipient\":\"+336xxxx\", \"ar\":\"Non recu\", \"ardate\":\"2013-03-03\",\"artime\":\"00:15:18\",\"arcode\":\"2\", \"errorcode\":\"UNDELIV\",\"errormsg\":\"Non livrable\"}}}";

	@Test
	public void testGet_arcode() {
		AckStatusSmsenvoi o = new AckStatusSmsenvoi();
		JsonNode json = RequestSmsenvoi.json_decode(checkdeliveryError_ar2);
		String arcode = o.get_arcode(json);
		
		assertEquals("2", arcode);
	}

}
