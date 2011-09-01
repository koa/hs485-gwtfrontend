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
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FloorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.RoomRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;
import ch.eleveneye.hs485.device.Registry;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

	private static final long		serialVersionUID	= 5816537750102063151L;
	private BuildingDao					dao;
	private FileDataRepository	fileDataRepository;
	private FloorRepository			floorRepository;
	private Registry						hs485registry;
	private RoomRepository			roomRepository;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#getFile
	 * (java.lang.String)
	 */
	public FileData getFile(final String filename) {
		return fileDataRepository.findOne(filename);
	}

	public List<OutputDevice> getOutputDevices() {
		return dao.list();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");
		final PlatformTransactionManager transactionManager = ctx.getBean("transactionManager", JpaTransactionManager.class);
		transactionTemplate = new TransactionTemplate(transactionManager);

		dao = ctx.getBean(BuildingDao.class);
		floorRepository = ctx.getBean(FloorRepository.class);
		roomRepository = ctx.getBean(RoomRepository.class);
		fileDataRepository = ctx.getBean(FileDataRepository.class);
		hs485registry = ctx.getBean("hs485registry", Registry.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#listAllFloors
	 * ()
	 */
	public Iterable<Floor> listAllFloors() {
		return floorRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#listAllRooms
	 * ()
	 */
	public Iterable<Room> listAllRooms() {
		return roomRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#listAllFiles
	 * ()
	 */
	public List<String> listFilesByMimeType(final String mimeType) {
		return fileDataRepository.listFilesByMimeType(mimeType);
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
				throw (SerializationException) ex.getCause();
			throw ex;
		}
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#removeFloors(java.lang.Iterable)
	 */
	public void removeFloors(final Iterable<Floor> floors) {
		floorRepository.delete(floors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#removeRooms
	 * (java.lang.Iterable)
	 */
	public void removeRooms(final Iterable<Room> rooms) {
		roomRepository.delete(rooms);
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#updateFloors(java.lang.Iterable)
	 */
	public void updateFloors(final Iterable<Floor> floors) {
		floorRepository.save(floors);
	}

	/**
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#updateRooms
	 * (java.lang.Iterable)
	 */
	public void updateRooms(final Iterable<Room> rooms) {
		roomRepository.save(rooms);
	}
}
