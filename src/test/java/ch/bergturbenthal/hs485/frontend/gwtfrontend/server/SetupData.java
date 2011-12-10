package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class SetupData {
	@Autowired
	private FileDataRepository	fileDataRepository;
	@Autowired
	private MongoOperations			mongoOps;
	@Autowired
	private PlanRepository			planRepository;

	private FileData loadFileFromClasspath(final String filename, final String mimeType) throws UnsupportedEncodingException, IOException {
		final FileData fileData = new FileData();
		fileData.setFileDataContent(readResource(new ClassPathResource(filename)));
		fileData.setFileName(filename);
		fileData.setMimeType(mimeType);
		fileDataRepository.save(fileData);
		return fileData;
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

	@Test
	public void setupData() throws UnsupportedEncodingException, IOException {
		mongoOps.dropCollection(Plan.class);
		mongoOps.dropCollection(FileData.class);
		final FileData file = loadFileFromClasspath("Stockwerk1_Grundriss.svg", "svg");
		final Plan plan = new Plan();
		plan.setName("Berg");
		final Floor floor = new Floor();
		floor.setPlan(file);
		plan.getFloors().add(floor);
		planRepository.save(plan);

	}
}
