/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.List;

import javax.persistence.PersistenceUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FloorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.OutputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;

@RunWith(SpringJUnit4ClassRunner.class)
@PersistenceUnit(unitName = "SpringJpaGettingStarted")
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class TestBuildingData {
	@Autowired
	private FileDataRepository			fileDataRepository;
	@Autowired
	private FloorRepository					floorRepository;
	@Autowired
	private OutputDeviceRepository	outputDeviceRepository;

	@Test
	public void testListFilenames() throws IOException {
		final FileData file = new FileData();
		file.setFileName("hello1");
		fileDataRepository.save(file);
		final List<String> files = fileDataRepository.listAllFiles();
		Assert.assertEquals(1, files.size());
		Assert.assertEquals("hello1", files.get(0));
	}

	@Test
	public void testSaveNewFloor() {
		final Floor floor = new Floor();
		floor.setName("1. Stockwerk");
		Assert.assertNull(floor.getFloorId());
		final Floor savedFloor = floorRepository.save(floor);
		Assert.assertNotNull(savedFloor.getFloorId());
	}

}
