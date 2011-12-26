package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.IconSetRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Connection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconEntry;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.PositionXY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class SetupData {
	private static final String	SVG_MIME	= "image/svg+xml";
	@Autowired
	private ConfigService				configService;
	@Autowired
	private FileDataRepository	fileDataRepository;
	@Autowired
	private IconSetRepository		iconSetRepository;
	@Autowired
	private MongoOperations			mongoOps;
	@Autowired
	private PlanRepository			planRepository;

	@Test
	public void setupData() throws UnsupportedEncodingException, IOException {
		mongoOps.dropCollection(Plan.class);
		mongoOps.dropCollection(FileData.class);
		mongoOps.dropCollection(IconSet.class);
		mongoOps.dropCollection(Connection.class);
		mongoOps.dropCollection(InputConnector.class);
		mongoOps.dropCollection(OutputDevice.class);
		final Plan plan = new Plan();
		plan.setIconSet(makeIconSet());
		plan.setName("Berg");
		final Floor floor = new Floor();
		floor.setName("1. Stockwerk");
		floor.setDrawing(loadFileFromClasspath("Stockwerk1_Grundriss.svg", SVG_MIME));
		floor.setIconSize(2000f);
		plan.getFloors().add(floor);

		final InputDevice inputDevice = new InputDevice();
		inputDevice.setName("Saeule");
		inputDevice.setPosition(new PositionXY(23600f, 13000f));
		final InputConnector inputConnector = new InputConnector();
		inputDevice.getConnectors().add(inputConnector);
		inputConnector.setType(InputDeviceType.SWITCH);
		inputConnector.setConnectorName("1L");

		floor.getInputDevices().add(inputDevice);

		final OutputDevice outputDevice = new OutputDevice();
		outputDevice.setName("Kueche");
		outputDevice.setPosition(new PositionXY(15000f, 6000f));
		outputDevice.setType(OutputDeviceType.LIGHT);

		floor.getOutputDevices().add(outputDevice);
		final Connection connection = new Connection();
		connection.setInputConnector(inputConnector);
		connection.setOutputDevice(outputDevice);
		plan.getConnections().add(connection);
		configService.savePlan(plan);

		// for (final Connection connection2 : plan.getConnections()) {
		// final InputConnector inputConnector2 = connection2.getInputConnector();
		// if (inputConnector2.getConnectorId() == null)
		// inputConnector2.setConnectorId(UUID.randomUUID().toString());
		// final OutputDevice outputDevice2 = connection2.getOutputDevice();
		// if (outputDevice2.getDeviceId() == null)
		// outputDevice2.setDeviceId(UUID.randomUUID().toString());
		// }
		//
		// planRepository.save(plan);

		System.out.println(plan.getIconSet());

		final Plan foundPlan = planRepository.findOne("plan");
		System.out.println(foundPlan.getConnections());
	}

	private FileData loadFileFromClasspath(final String filename, final String mimeType) throws UnsupportedEncodingException, IOException {
		final FileData fileData = new FileData();
		fileData.setFileDataContent(readResource(new ClassPathResource(filename)));
		fileData.setFileName(filename);
		fileData.setMimeType(mimeType);
		fileDataRepository.save(fileData);
		return fileData;
	}

	private IconSet makeIconSet() throws UnsupportedEncodingException, IOException {
		final IconSet iconSet = new IconSet();
		iconSet.setInputIcon(new IconEntry(loadFileFromClasspath("symbols/switch_2.svg", SVG_MIME)));
		final Map<OutputDeviceType, IconEntry> outputIcons = iconSet.getOutputIcons();
		outputIcons.put(OutputDeviceType.LIGHT, new IconEntry(loadFileFromClasspath("symbols/bulb_on.svg", SVG_MIME)));
		outputIcons.put(OutputDeviceType.FAN, new IconEntry(loadFileFromClasspath("symbols/fan.svg", SVG_MIME)));
		outputIcons.put(OutputDeviceType.HEAT, new IconEntry(loadFileFromClasspath("symbols/glossy_flame.svg", SVG_MIME)));

		iconSet.setIconsetId(UUID.randomUUID().toString());
		iconSet.setName("Default");
		return iconSetRepository.save(iconSet);
	}

	private String readResource(final Resource resource) throws UnsupportedEncodingException, IOException {
		final InputStreamReader reader = new InputStreamReader(resource.getInputStream(), "utf-8");
		final StringBuffer stringBuffer = new StringBuffer();
		final char[] buffer = new char[8192];
		while (true) {
			final int read = reader.read(buffer);
			if (read < 0)
				break;
			stringBuffer.append(buffer, 0, read);
		}
		final String data = stringBuffer.toString();
		return data;
	}
}
