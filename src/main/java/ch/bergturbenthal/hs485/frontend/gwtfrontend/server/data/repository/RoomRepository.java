/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;

/**
 *
 */
public interface RoomRepository extends CrudRepository<Room, Integer> {
	public List<Room> findByFloor(Floor floor);
}
