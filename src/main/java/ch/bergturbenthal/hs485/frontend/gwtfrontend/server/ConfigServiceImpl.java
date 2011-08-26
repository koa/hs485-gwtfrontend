package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigServiceImpl extends RemoteServiceServlet implements ConfigService {

	public List<OutputDevice> getOutputDevices() {
		// TODO Auto-generated method stub
		return null;
	}
}
