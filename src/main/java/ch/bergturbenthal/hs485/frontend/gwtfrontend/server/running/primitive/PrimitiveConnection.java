package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive;

public class PrimitiveConnection {
	private PrimitiveEventSource	source;
	private PrimitiveEventSink		sink;

	public PrimitiveEventSink getSink() {
		return sink;
	}

	public PrimitiveEventSource getSource() {
		return source;
	}

	public void setSink(final PrimitiveEventSink sink) {
		this.sink = sink;
	}

	public void setSource(final PrimitiveEventSource event) {
		this.source = event;
	}
}
