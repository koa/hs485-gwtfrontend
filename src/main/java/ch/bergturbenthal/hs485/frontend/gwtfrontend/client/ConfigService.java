package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

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

	public List<OutputDevice> getOutputDevices();
}
