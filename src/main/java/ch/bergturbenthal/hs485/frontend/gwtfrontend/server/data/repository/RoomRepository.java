/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Room;

/**
 *
 */
public interface RoomRepository extends CrudRepository<Room, Integer> {
	public List<Room> findByFloor(Floor floor);
}
