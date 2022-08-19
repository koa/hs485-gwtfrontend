/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ch/bergturbenthal/hs485/frontend/gwtfrontend/server/testContext.xml" })
public class TestBuildingData {
	@Autowired
	private FileDataRepository	fileDataRepository;
	@Autowired
	private PlanRepository			planRepository;

	@Test
	@Ignore
	public void testListFilenames() throws IOException {
		final FileData file = new FileData();
		file.setFileName("hello1");
		fileDataRepository.save(file);
		final Iterable<FileData> filesIter = fileDataRepository.findAll();
		final List<FileData> files = new ArrayList<FileData>();
		final Set<String> filenames = new HashSet<String>();
		for (final FileData fileData : filesIter) {
			files.add(fileData);
			filenames.add(fileData.getFileName());
		}
		Assert.assertTrue(filenames.contains("hello1"));
	}
}
