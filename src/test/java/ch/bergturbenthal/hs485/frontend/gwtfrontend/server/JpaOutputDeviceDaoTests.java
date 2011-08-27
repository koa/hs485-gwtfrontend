package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import org.springframework.test.jpa.AbstractJpaTests;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.OutputDeviceDao;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice.Type;

public class JpaOutputDeviceDaoTests extends AbstractJpaTests {
	private OutputDeviceDao	outputDeviceDao;

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/applicationContext.xml" };
	}

	public void setOutputDeviceDao(final OutputDeviceDao outputDeviceDao) {
		this.outputDeviceDao = outputDeviceDao;
	}

	protected void setUpInTransaction() {
		jdbcTemplate.execute("INSERT INTO outputdevice (deviceid,name,type) values(1,'Hello',0)");
	}

	public void testSaveOutputDevice() {
		final OutputDevice device = new OutputDevice();
		device.setName("Hello World");
		device.setType(Type.DIMMER);
		final int deviceId = device.getDeviceId();
		outputDeviceDao.save(device);
		assertFalse(deviceId == device.getDeviceId());
	}
}
