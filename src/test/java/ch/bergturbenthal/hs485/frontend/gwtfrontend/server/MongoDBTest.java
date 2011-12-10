package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class MongoDBTest {
	@Autowired
	private PlanRepository	planRepository;
	@Autowired
	private MongoOperations	mongoOps;

	@Test
	public void initMongoDb() {
		mongoOps.dropCollection(Floor.class);
		final Floor floor = new Floor();
		floor.setName("EG");
		final InputDevice input = new InputDevice();
		input.setName("Schalter 3");
		input.getPosition().setX((float) 1);
		input.getPosition().setY((float) 2);
		input.setType(InputDeviceType.SWITCH);
		final InputConnector inputConnector = new InputConnector();
		inputConnector.setConnectorName("1L");
		inputConnector.getAddress().setDeviceAddress(123);
		inputConnector.getAddress().setInputAddress(2);
		input.getConnectors().add(inputConnector);

		floor.getInputDevices().add(input);
		final Plan plan = new Plan();
		plan.getFloors().add(floor);
		mongoOps.save(input);
		mongoOps.save(plan);

		final List<Plan> all = mongoOps.findAll(Plan.class);
		for (final Plan plan2 : all) {
			System.out.println(plan2);
			for (final Floor floor2 : plan2.getFloors())
				System.out.println(" Floor " + floor2);
		}
		Assert.assertFalse(all.size() == 0);
	}
}
