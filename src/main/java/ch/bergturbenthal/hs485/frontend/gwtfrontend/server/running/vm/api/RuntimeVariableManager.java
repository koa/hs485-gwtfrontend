package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;

public interface RuntimeVariableManager {

	<V extends Value> void addChangeListener(final Variable<V> variable, ChangeListener<V> handler);

	<V extends Value> V readVariable(final Variable<V> variable);

	<V extends Value> void writeVariable(final Variable<V> variable, final V value);

}