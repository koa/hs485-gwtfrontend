package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.dummy.SerializationHelperDummy;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.StorageService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.BuildingService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

public class ConfigServiceImpl extends AutowiringRemoteServiceServlet implements ConfigService {

	private static final long	serialVersionUID	= 5816537750102063151L;
	@Autowired
	private BuildingService		configurationService;

	@Autowired
	private StorageService		storageService;

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
		return storageService.getFile(filename);
	}

	@Override
	public Map<String, String> listAllPlans() {
		return storageService.listAllPlans();
	}

	@Override
	public List<String> listFilesByMime(final String mime) {
		return storageService.listFilesByMime(mime);
	}

	@Override
	public Plan loadCurrentPlan() {
		return storageService.getRunningPlan();
	}

	@Override
	public List<IconSet> loadIconSets() {
		return storageService.loadIconSets();
	}

	@Override
	public Plan readExistingConnections(final Plan plan) {
		configurationService.appendExistingConnections(plan);
		return plan;
	}

	@Override
	public Plan readPlan(final String planId) {
		return storageService.readPlan(planId);
	}

	@Override
	public void saveIconsets(final List<IconSet> iconSets) {
		storageService.saveIconsets(iconSets);
	}

	@Override
	public Plan savePlan(final Plan plan) {
		return storageService.savePlan(plan);
	}
}
