/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;

/**
 *
 */
public interface OutputDeviceRepository extends CrudRepository<OutputDevice, Integer> {
	public List<OutputDevice> findByRoom(Room room);
}
