package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {

	/**
	 * Load Spring Context
	 * 
	 * @return
	 */
	public static ApplicationContext getSpringContext() {
		return new ClassPathXmlApplicationContext("ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");
	}

}
