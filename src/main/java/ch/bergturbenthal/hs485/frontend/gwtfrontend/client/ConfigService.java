package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ConfigService")
public interface ConfigService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ConfigServiceAsync	instance;

		public static ConfigServiceAsync getInstance() {
			if (instance == null)
				instance = GWT.create(ConfigService.class);
			return instance;
		}
	}

	public FileData getFile(String filename);

	public Iterable<OutputDevice> getOutputDevices();

	public Iterable<OutputDevice> getOutputDevicesByFloor(Floor floor);

	/**
	 * Gets all Available Floors
	 * 
	 * @return
	 */
	public Iterable<Floor> listAllFloors();

	public List<String> listFilesByMimeType(String mimeType);

	/**
	 * remove this Floors
	 * 
	 * @param floors
	 */
	public void removeFloors(Iterable<Floor> floors);

	public void removeOutputDevice(OutputDevice device);

	/**
	 * Add or modifiy this floors
	 * 
	 * @param floors
	 */
	public void updateFloors(Iterable<Floor> floors);

	public Iterable<OutputDevice> updateOutputDevices(Iterable<OutputDevice> devices);

}
