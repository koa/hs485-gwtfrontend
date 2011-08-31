package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

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

	public void addOutputDevice(OutputDevice device);

	public List<OutputDevice> getOutputDevices();

	public Iterable<Floor> listAllFloors();

	/**
	 * rmeove this Floors
	 * 
	 * @param floors
	 */
	public void removeFloors(Iterable<Floor> floors);

	/**
	 * Add or modifiy this floors
	 * 
	 * @param floors
	 */
	public void updateFloors(Iterable<Floor> floors);

	public void updateOutputDevice(OutputDevice device);
}
