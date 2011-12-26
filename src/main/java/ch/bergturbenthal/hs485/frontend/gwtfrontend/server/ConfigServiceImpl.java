package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.IconSetRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.InputConnectorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.OutputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Connection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

@Transactional
public class ConfigServiceImpl extends AutowiringRemoteServiceServlet implements ConfigService {

	private static final long					serialVersionUID	= 5816537750102063151L;

	@Autowired
	private FileDataRepository				fileDataRepository;
	@Autowired
	private IconSetRepository					iconSetRepository;
	@Autowired
	private InputConnectorRepository	inputConnectorRepository;
	@Autowired
	private OutputDeviceRepository		outputDeviceRepository;
	@Autowired
	private PlanRepository						planRepository;

	public FileData getFile(final String filename) {
		return fileDataRepository.findOne(filename);
	}

	public Map<String, String> listAllPlans() {
		final HashMap<String, String> ret = new HashMap<String, String>();
		for (final Plan plan : planRepository.findAll())
			ret.put(plan.getPlanId(), plan.getName());
		return ret;
	}

	@Override
	public List<String> listFilesByMime(final String mime) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (final FileData file : fileDataRepository.findAll())
			if (file.getMimeType().equals(mime))
				ret.add(file.getFileName());
		return ret;
	}

	@Override
	public List<IconSet> loadIconSets() {
		final ArrayList<IconSet> ret = new ArrayList<IconSet>();
		for (final IconSet iconSet : iconSetRepository.findAll())
			ret.add(iconSet);
		return ret;
	}

	@Override
	public Plan readPlan(final String planId) {
		final Plan plan = planRepository.findOne(planId);
		System.out.println(plan.getConnections());
		return plan;
	}

	@Override
	public Plan savePlan(final Plan plan) {
		try {
			System.out.println(plan.getConnections());
			for (final Connection connection : plan.getConnections()) {
				final InputConnector inputConnector = connection.getInputConnector();
				if (inputConnector.getConnectorId() == null)
					inputConnector.setConnectorId(UUID.randomUUID().toString());
				inputConnectorRepository.save(inputConnector);
				final OutputDevice outputDevice = connection.getOutputDevice();
				if (outputDevice.getDeviceId() == null)
					outputDevice.setDeviceId(UUID.randomUUID().toString());
				outputDeviceRepository.save(outputDevice);
			}
			return planRepository.save(plan);
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}
}
