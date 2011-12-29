package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import javax.servlet.ServletException;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Helper for autowiring of ServiceImpls
 * 
 * @author akoenig
 * 
 */
public abstract class AutowiringRemoteServiceServlet extends RemoteServiceServlet {

	@Override
	public void init() throws ServletException {
		super.init();
		final WebApplicationContext requiredWebApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		final AutowireCapableBeanFactory autowireCapableBeanFactory = requiredWebApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}

}
