/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;

/**
 *
 */
public interface InputDeviceRepository extends CrudRepository<InputDevice, Integer> {
}
