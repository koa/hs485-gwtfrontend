package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.PlanRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

@Transactional
public class ConfigServiceImpl extends AutowiringRemoteServiceServlet implements ConfigService {

	private static final long		serialVersionUID	= 5816537750102063151L;

	@Autowired
	private FileDataRepository	fileDataRepository;
	// @Autowired
	// private InputDeviceRepository inputDeviceRepository;
	// @Autowired
	// private OutputDeviceRepository outputDeviceRepository;
	@Autowired
	private PlanRepository			planRepository;

	// @Autowired
	// private TransactionTemplate transactionTemplate;

	public FileData getFile(final String filename) {
		return fileDataRepository.findOne(filename);
	}

	public Map<String, String> listAllPlans() {
		final HashedMap<String, String> ret = new HashedMap<String, String>();
		for (final Plan plan : planRepository.findAll())
			ret.put(plan.getPlanId(), plan.getName());
		return ret;
	}

	@Override
	public List<String> listFilesByMime(final String mime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Plan readPlan(final String planId) {
		return planRepository.findOne(planId);
	}

	@Override
	public Plan savePlan(final Plan plan) {
		return planRepository.save(plan);
	}
}
