/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

/**
 *
 */
public interface OutputDeviceRepository extends CrudRepository<OutputDevice, Integer> {
	@Query("select od from OutputDevice od where od.floorPlace.floor=:floor")
	public List<OutputDevice> findByFloor(@Param("floor") Floor floor);
}
