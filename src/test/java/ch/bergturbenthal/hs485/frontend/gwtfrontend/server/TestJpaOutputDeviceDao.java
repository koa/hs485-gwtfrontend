package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import javax.persistence.PersistenceUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.OutputDeviceDao;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice.Type;

@RunWith(SpringJUnit4ClassRunner.class)
@PersistenceUnit(unitName = "SpringJpaGettingStarted")
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
// @Transactional
public class TestJpaOutputDeviceDao {

	@Autowired
	private OutputDeviceDao	outputdeviceDao;

	@Test
	public void testSaveOutputDevice() {
		final OutputDevice device = new OutputDevice();
		device.setName("Hello World");
		device.setType(Type.DIMMER);
		// final int deviceId = device.getDeviceId();
		// outputDeviceDao.save(device);
		outputdeviceDao.insert(device);
		// Assert.assertFalse(deviceId == device.getDeviceId());
	}
}
