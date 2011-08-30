/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import javax.persistence.PersistenceUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FloorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;

@RunWith(SpringJUnit4ClassRunner.class)
@PersistenceUnit(unitName = "SpringJpaGettingStarted")
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class TestFloor {
	@Autowired
	private FloorRepository	floorRepository;

	@Test
	public void testSaveNewFloor() {
		final Floor floor = new Floor();
		floor.setName("1. Stockwerk");
		Assert.assertNull(floor.getFloorId());
		final Floor savedFloor = floorRepository.save(floor);
		Assert.assertNotNull(savedFloor.getFloorId());
	}

}
