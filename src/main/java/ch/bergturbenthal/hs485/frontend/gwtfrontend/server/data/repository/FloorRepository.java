/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;

@Transactional
public interface FloorRepository extends CrudRepository<Floor, Integer> {

}
