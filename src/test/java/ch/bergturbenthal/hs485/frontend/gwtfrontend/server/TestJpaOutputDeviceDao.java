package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import javax.persistence.PersistenceUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.BuildingDao;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;

@RunWith(SpringJUnit4ClassRunner.class)
@PersistenceUnit(unitName = "SpringJpaGettingStarted")
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
// @Transactional
public class TestJpaOutputDeviceDao {

	@Autowired
	private BuildingDao	outputdeviceDao;

	@Test
	public void testSaveOutputDevice() {
		final OutputDevice device = new OutputDevice();
		device.setName("Hello World");
		device.setType(OutputDeviceType.DIMMER);
		// final int deviceId = device.getDeviceId();
		// outputDeviceDao.save(device);
		outputdeviceDao.insert(device);
		// Assert.assertFalse(deviceId == device.getDeviceId());
	}
}
