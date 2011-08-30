package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

@Repository
@Transactional
public class JpaOutputDeviceImpl implements OutputDeviceDao {

	@PersistenceContext
	private EntityManager	em;

	public void insert(final OutputDevice device) {
		em.persist(device);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.OutputDeviceDao
	 * #listOutputDevices()
	 */
	public List<OutputDevice> listOutputDevices() {
		final TypedQuery<OutputDevice> query = em.createQuery("SELECT od from OutputDevice od", OutputDevice.class);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.OutputDeviceDao
	 * #update(ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice)
	 */
	public OutputDevice update(final OutputDevice device) {
		return em.merge(device);
	}

}
