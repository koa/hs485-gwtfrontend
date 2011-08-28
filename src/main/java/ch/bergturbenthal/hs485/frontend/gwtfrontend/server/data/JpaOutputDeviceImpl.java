package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

@Repository
@Transactional
public class JpaOutputDeviceImpl implements OutputDeviceDao {

	@PersistenceContext
	private EntityManager	em;

	public void save(final OutputDevice device) {
		em.persist(device);
	}

}
