package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.dummy.SerializationHelperDummy;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.IconSetRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.InputConnectorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.OutputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.BuildingService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

@Transactional
public class ConfigServiceImpl extends AutowiringRemoteServiceServlet implements ConfigService {

	private static final long					serialVersionUID	= 5816537750102063151L;

	@Autowired
	private BuildingService						configurationService;
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

	@Override
	public void activatePlan(final Plan plan) {
		configurationService.activatePlan(plan);
	}

	@Override
	public void dummyOperation(final SerializationHelperDummy dummyRequest) {
		// Dummy for solving serialization-problems

	}

	@Override
	public FileData getFile(final String filename) {
		return fileDataRepository.findOne(filename);
	}

	@Override
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
			if (mime.equals(file.getMimeType()))
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
	public Plan readExistingConnections(final Plan plan) {
		configurationService.appendExistingConnections(plan);
		return plan;
	}

	@Override
	public Plan readPlan(final String planId) {
		final Plan plan = planRepository.findOne(planId);
		return plan;
	}

	@Override
	public void saveIconsets(final List<IconSet> iconSets) {
		final Set<String> savedIconSets = new HashSet<String>();
		for (final IconSet iconSet : iconSetRepository.save(iconSets))
			savedIconSets.add(iconSet.getIconsetId());
		for (final IconSet iconSet : iconSetRepository.findAll())
			if (!savedIconSets.contains(iconSet.getIconsetId()))
				iconSetRepository.delete(iconSet);

	}

	@Override
	public Plan savePlan(final Plan plan) {
		try {
			final Map<InputConnector, Boolean> inputConnectorMap = new IdentityHashMap<InputConnector, Boolean>();
			final Map<OutputDevice, Boolean> outputDeviceMap = new IdentityHashMap<OutputDevice, Boolean>();

			for (final Floor floor : plan.getFloors()) {
				for (final InputDevice inputDevice : floor.getInputDevices())
					for (final InputConnector inputConnector : inputDevice.getConnectors()) {
						saveInputConnector(inputConnector);
						inputConnectorMap.put(inputConnector, true);
					}
				for (final OutputDevice outputDevice : floor.getOutputDevices()) {
					saveOutputDevice(outputDevice);
					outputDeviceMap.put(outputDevice, true);
				}
			}
			// clean orphan refs

			return planRepository.save(plan);
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	private void saveInputConnector(final InputConnector inputConnector) {
		if (inputConnector.getConnectorId() == null)
			inputConnector.setConnectorId(UUID.randomUUID().toString());
		inputConnectorRepository.save(inputConnector);
	}

	private void saveOutputDevice(final OutputDevice outputDevice) {
		if (outputDevice.getDeviceId() == null)
			outputDevice.setDeviceId(UUID.randomUUID().toString());
		outputDeviceRepository.save(outputDevice);
	}
}
