package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import java.util.Collection;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;

public interface BuildingDao {
	/**
	 * Add a new Output-Device to the Persistence
	 * 
	 * @param device
	 */
	public void insert(OutputDevice device);

	public List<OutputDevice> list();

	/**
	 * gives all Floors
	 * 
	 * @return
	 */
	public Collection<Floor> listFloors();

	/**
	 * Makes a new Floor and Returns it
	 * 
	 * @return new Floor
	 */
	public Floor newFloor();

	/**
	 * 
	 * @return new Room
	 */
	public Room newRoom();

	/**
	 * Updates a existing OutputDevice
	 * 
	 * @param device
	 */
	public OutputDevice update(OutputDevice device);

	/**
	 * Updates a Floor
	 * 
	 * @param floor
	 * @return
	 */
	public Floor updateFloor(Floor floor);

	/**
	 * Updates a Room
	 * 
	 * @param room
	 * @return
	 */
	public Room updateRoom(Room room);
}
