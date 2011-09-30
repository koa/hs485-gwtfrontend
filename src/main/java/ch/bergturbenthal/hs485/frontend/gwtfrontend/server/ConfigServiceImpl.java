package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FloorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.InputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.OutputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.eleveneye.hs485.api.BroadcastHandler;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.protocol.IMessage;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

	private static final long				serialVersionUID	= 5816537750102063151L;
	private FileDataRepository			fileDataRepository;
	private FloorRepository					floorRepository;
	private Registry								hs485registry;
	private InputDeviceRepository		inputDeviceRepository;
	private OutputDeviceRepository	outputDeviceRepository;
	private TransactionTemplate			transactionTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#
	 * addOutputDevice
	 * (ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice)
	 */
	public void addOutputDevice(final OutputDevice device) {
		outputDeviceRepository.save(device);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#
	 * getInputDevicesByFloor
	 * (ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor)
	 */
	@Override
	public Iterable<InputDevice> getInputDevicesByFloor(final Floor floor) {
		return makeSerializable(inputDeviceRepository.findByFloor(floor));
	}

	public Iterable<OutputDevice> getOutputDevices() {
		return outputDeviceRepository.findAll();
	}

	@Override
	public Iterable<OutputDevice> getOutputDevicesByFloor(final Floor floor) {
		return outputDeviceRepository.findByFloor(floor);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");
		final PlatformTransactionManager transactionManager = ctx.getBean("transactionManager", JpaTransactionManager.class);
		transactionTemplate = new TransactionTemplate(transactionManager);
		outputDeviceRepository = ctx.getBean(OutputDeviceRepository.class);
		inputDeviceRepository = ctx.getBean(InputDeviceRepository.class);
		floorRepository = ctx.getBean(FloorRepository.class);
		fileDataRepository = ctx.getBean(FileDataRepository.class);
		hs485registry = ctx.getBean("hs485registry", Registry.class);
		hs485registry.getBus().addBroadcastHandler(new BroadcastHandler() {

			@Override
			public void handleBroadcastMessage(final IMessage message) {
				// BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class,
				// "GWT").broadcast(new StreamingServiceBusiness.Event(queueName,
				// message));
				System.out.println("Message");
				System.out.println("From: " + message.getSenderNumber());
				System.out.println(message);
				new JerseyBroadcaster(AtmosphereGwtHandler.GWT_BROADCASTER_ID).broadcast(message);
				// BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class,
				// AtmosphereGwtHandler.GWT_BROADCASTER_ID).broadcast(message);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#listAllFloors
	 * ()
	 */
	public Iterable<Floor> listAllFloors() {
		System.out.println("---------------------- list all floors ------------------------------");
		try {
			final Iterable<Floor> found = floorRepository.findAll();
			System.out.println(found);
			System.out.println("----- returning -----");
			return found;
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw e;
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
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

	/**
	 * @param iterable
	 * @return
	 */
	private Iterable<InputDevice> makeSerializable(final Iterable<InputDevice> iterable) {
		for (final InputDevice inputDevice : iterable) {
			final List<InputConnector> connectors = inputDevice.getConnectors();
			if (connectors != null)
				inputDevice.setConnectors(new ArrayList<InputConnector>(connectors));
		}
		return iterable;
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
			ex.printStackTrace();
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
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#
	 * removeInputDevice
	 * (ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice)
	 */
	@Override
	public void removeInputDevice(final InputDevice device) {
		inputDeviceRepository.delete(device);
	}

	@Override
	public void removeOutputDevice(final OutputDevice device) {
		outputDeviceRepository.delete(device);
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#updateFloors(java.lang.Iterable)
	 */
	public void updateFloors(final Iterable<Floor> floors) {
		System.out.println("----------------------- update floors ----------------");
		System.out.println(floors);
		floorRepository.save(floors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#
	 * updateInputDevices(java.lang.Iterable)
	 */
	@Override
	public Iterable<InputDevice> updateInputDevices(final Iterable<InputDevice> devices) {
		return makeSerializable(inputDeviceRepository.save(devices));
	}

	/**
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService#addOutputDevice(ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice)
	 */
	public void updateOutputDevice(final OutputDevice device) {
		outputDeviceRepository.save(device);
	}

	@Override
	public Iterable<OutputDevice> updateOutputDevices(final Iterable<OutputDevice> devices) {
		System.out.println("--------------- update output devices ---------------");
		System.out.println(devices);
		return outputDeviceRepository.save(devices);
	}
}
