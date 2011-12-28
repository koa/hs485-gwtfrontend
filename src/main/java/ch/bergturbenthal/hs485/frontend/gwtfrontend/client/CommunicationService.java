package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("CommunicationService")
public interface CommunicationService extends RemoteService {
	Collection<Event> getEvents();

	Map<InputAddress, InputDescription> listInputDevices();

	Map<OutputAddress, OutputDescription> listOutputDevices();

	float readHmuidity(InputAddress address);

	float readTemperature(InputAddress address);

}
