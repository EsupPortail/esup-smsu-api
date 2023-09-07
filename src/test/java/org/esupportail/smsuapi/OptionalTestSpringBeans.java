package org.esupportail.smsuapi;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/* not run by default. You can use the following to run it: 
   mvn -Dtest=OptionalTestSpringBeans test
*/
public class OptionalTestSpringBeans {

	@Test
	public void testSpringBeans() {
		@SuppressWarnings({ "unused", "resource" })
		ClassPathXmlApplicationContext unused = new ClassPathXmlApplicationContext("properties/applicationContext.xml");
	}

}
