package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.IconSetRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.InputConnectorRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.OutputDeviceRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.RunningConfigurationRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.RunningConfiguration;

@Service
public class StorageService {
	@Autowired
	private FileDataRepository							fileDataRepository;
	@Autowired
	private IconSetRepository								iconSetRepository;
	@Autowired
	private InputConnectorRepository				inputConnectorRepository;
	@Autowired
	private OutputDeviceRepository					outputDeviceRepository;
	@Autowired
	private PlanRepository									planRepository;
	@Autowired
	private RunningConfigurationRepository	runningConfigurationRepository;
	private static Logger										logger	= LoggerFactory.getLogger(StorageService.class);

	public FileData getFile(final String filename) {
		return fileDataRepository.findOne(filename);
	}

	public Plan getRunningPlan() {
		final RunningConfiguration runningConfiguration = runningConfigurationRepository.findOne("running");
		logger.info("Running Configuration: " + runningConfiguration);
		if (runningConfiguration != null) {
			final String planId = runningConfiguration.getPlanId();
			if (planId != null)
				return readPlan(planId);
		}
		return null;
	}

	public Map<String, String> listAllPlans() {
		final HashMap<String, String> ret = new HashMap<String, String>();
		for (final Plan plan : planRepository.findAll())
			ret.put(plan.getPlanId(), plan.getName());
		return ret;
	}

	public List<String> listFilesByMime(final String mime) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (final FileData file : fileDataRepository.findAll())
			if (mime.equals(file.getMimeType()))
				ret.add(file.getFileName());
		return ret;
	}

	public List<IconSet> loadIconSets() {
		final ArrayList<IconSet> ret = new ArrayList<IconSet>();
		for (final IconSet iconSet : iconSetRepository.findAll())
			ret.add(iconSet);
		return ret;
	}

	public Plan readPlan(final String planId) {
		return planRepository.findOne(planId);
	}

	public void saveAsRunning(final Plan plan) {
		final Plan saved = savePlan(plan);
		final RunningConfiguration runningConfiguration = new RunningConfiguration();
		runningConfiguration.setPlanId(saved.getPlanId());
		runningConfiguration.setId("running");
		runningConfigurationRepository.save(runningConfiguration);
		logger.info("Saved: " + runningConfiguration);
	}

	public void saveIconsets(final List<IconSet> iconSets) {
		final Set<String> savedIconSets = new HashSet<String>();
		for (final IconSet iconSet : iconSetRepository.save(iconSets))
			savedIconSets.add(iconSet.getIconsetId());
		for (final IconSet iconSet : iconSetRepository.findAll())
			if (!savedIconSets.contains(iconSet.getIconsetId()))
				iconSetRepository.delete(iconSet);

	}

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
