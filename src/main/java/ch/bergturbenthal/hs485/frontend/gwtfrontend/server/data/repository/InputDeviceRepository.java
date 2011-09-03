/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;

/**
 *
 */
public interface InputDeviceRepository extends CrudRepository<InputDevice, Integer> {
	@Query("select id from InputDevice id where id.floorPlace.floor=:floor")
	public List<InputDevice> findByFloor(@Param("floor") Floor floor);
}
