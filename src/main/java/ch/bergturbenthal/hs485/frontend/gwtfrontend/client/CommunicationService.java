package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Event;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("CommunicationService")
public interface CommunicationService extends RemoteService {
	public Collection<Event> getEvents();
}
