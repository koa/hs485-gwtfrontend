package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.List;

import javax.servlet.ServletException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FloorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

	private static final long		serialVersionUID	= 5816537750102063151L;
	private BuildingDao					dao;
	private FloorRepository			floorRepository;
	private TransactionTemplate	transactionTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#
	 * addOutputDevice
	 * (ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice)
	 */
	public void addOutputDevice(final OutputDevice device) {
		dao.insert(device);
	}

	public List<OutputDevice> getOutputDevices() {
		return dao.list();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");
		dao = ctx.getBean(BuildingDao.class);
		floorRepository = ctx.getBean(FloorRepository.class);
		final PlatformTransactionManager transactionManager = ctx.getBean("transactionManager", JpaTransactionManager.class);
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

	@Override
	public String processCall(final String payload) throws SerializationException {
		try {
			return transactionTemplate.execute(new TransactionCallback<String>() {

				public String doInTransaction(final TransactionStatus status) {
					try {
						return ConfigServiceImpl.super.processCall(payload);
					} catch (final SerializationException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (final RuntimeException ex) {
			if (ex.getCause() != null && ex.getCause() instanceof SerializationException)
				throw (RuntimeException) ex.getCause();
			throw ex;
		}
	}

	/**
	 * 
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#addOutputDevice(ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice)
	 */
	public void updateOutputDevice(final OutputDevice device) {
		transactionTemplate.execute(new TransactionCallback<OutputDevice>() {

			public OutputDevice doInTransaction(final TransactionStatus status) {
				return dao.update(device);
			}
		});
	}
}
