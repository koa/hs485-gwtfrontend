package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

public interface OutputDeviceDao {
	/**
	 * Add a new Output-Device to the Persistence
	 * 
	 * @param device
	 */
	public void insert(OutputDevice device);

	public List<OutputDevice> listOutputDevices();

	/**
	 * Updates a existing OutputDevice
	 * 
	 * @param device
	 */
	public OutputDevice update(OutputDevice device);
}
