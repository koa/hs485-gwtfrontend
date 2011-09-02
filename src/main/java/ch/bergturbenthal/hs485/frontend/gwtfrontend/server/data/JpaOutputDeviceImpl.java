package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Room;

@Repository
@Transactional
public class JpaOutputDeviceImpl implements BuildingDao {

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
	public List<OutputDevice> list() {
		final TypedQuery<OutputDevice> query = em.createQuery("SELECT od from OutputDevice od", OutputDevice.class);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao#listFloors
	 * ()
	 */
	public Collection<Floor> listFloors() {
		return em.createQuery("select fl from Floor fl", Floor.class).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao#newFloor
	 * ()
	 */
	public Floor newFloor() {
		final Floor floor = new Floor();
		em.persist(floor);
		return floor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao#newRoom
	 * ()
	 */
	public Room newRoom() {
		final Room room = new Room();
		em.persist(room);
		return room;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao#
	 * updateFloor(ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor)
	 */
	public Floor updateFloor(final Floor floor) {
		return em.merge(floor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao#updateRoom
	 * (ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room)
	 */
	public Room updateRoom(final Room room) {
		return em.merge(room);
	}

}
