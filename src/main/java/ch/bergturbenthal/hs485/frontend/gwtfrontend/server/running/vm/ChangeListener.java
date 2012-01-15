package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;

public interface ChangeListener<V extends Value> {
	void valueUpdated(V oldValue, V newValue);
}
