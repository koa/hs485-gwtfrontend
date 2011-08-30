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
		transactionTemplate.execute(new TransactionCallback<Void>() {

			public Void doInTransaction(final TransactionStatus status) {
				dao.insert(device);
				return null;
			}
		});
	}

	public List<OutputDevice> getOutputDevices() {
		try {
			return dao.list();
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
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
