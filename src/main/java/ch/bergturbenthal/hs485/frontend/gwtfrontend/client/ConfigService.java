package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.List;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ConfigService")
public interface ConfigService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ConfigServiceAsync	instance;

		public static ConfigServiceAsync getInstance() {
			if (instance == null)
				instance = GWT.create(ConfigService.class);
			return instance;
		}
	}

	FileData getFile(String filename);

	Map<String, String> listAllPlans();

	List<String> listFilesByMime(String mime);

	List<IconSet> loadIconSets();

	Plan readPlan(String planId);

	void saveIconsets(List<IconSet> iconSets);

	Plan savePlan(Plan plan);
}
