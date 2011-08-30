/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;

/**
 *
 */
public interface InputDeviceRepository extends CrudRepository<InputDevice, Integer> {
	public List<InputDevice> findByRoom(Room room);
}
