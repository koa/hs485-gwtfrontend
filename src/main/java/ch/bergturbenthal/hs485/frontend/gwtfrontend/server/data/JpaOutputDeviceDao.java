package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import org.springframework.orm.jpa.support.JpaDaoSupport;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

public class JpaOutputDeviceDao extends JpaDaoSupport implements OutputDeviceDao {

	public void save(final OutputDevice device) {
		getJpaTemplate().persist(device);
	}

}
