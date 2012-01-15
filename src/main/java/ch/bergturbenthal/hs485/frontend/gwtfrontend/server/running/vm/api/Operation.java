package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api;

public interface Operation<O extends Operation<O>> {
	OperationDescriptor<O> getDesriptor();
}
