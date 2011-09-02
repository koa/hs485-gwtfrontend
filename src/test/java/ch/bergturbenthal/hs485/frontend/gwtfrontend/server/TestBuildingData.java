/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.Collection;
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
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.RoomRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Room;

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
	@Autowired
	private RoomRepository					roomRepository;

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
	public void testLoadRoomByFloor() {
		final Floor floor = new Floor();
		final Room room = new Room();
		room.setFloor(floor);
		final Floor savedFloor = floorRepository.save(floor);
		final Room savedRoom = roomRepository.save(room);
		final Collection<Room> foundRooms = roomRepository.findByFloor(savedFloor);
		Assert.assertEquals(1, foundRooms.size());
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
