package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import ch.eleveneye.hs485.device.Registry;

public interface Factory<T> {
	T makeObject();

	void setRegistry(Registry registry);
}
