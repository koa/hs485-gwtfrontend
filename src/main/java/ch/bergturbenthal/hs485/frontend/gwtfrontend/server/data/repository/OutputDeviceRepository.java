/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

/**
 *
 */
public interface OutputDeviceRepository extends CrudRepository<OutputDevice, Integer> {
	public List<OutputDevice> findByFloor(Floor floor);
}
