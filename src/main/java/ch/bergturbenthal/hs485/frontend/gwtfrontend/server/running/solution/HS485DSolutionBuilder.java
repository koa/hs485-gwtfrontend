package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveOutputDeviceKeyEventSink;
import ch.eleveneye.hs485.device.Registry;

public class HS485DSolutionBuilder extends AbstractSolutionBuilder implements SolutionBuilder {

	public HS485DSolutionBuilder(final Registry registry, final ScheduledExecutorService executorService) {
		super(registry, executorService);
	}

	@Override
	public Collection<ConfigSolutionPrimitive> makeSinkSolutionVariants(final PrimitiveConnection connection) {
		try {
			if (connection.getSink() instanceof PrimitiveOutputDeviceKeyEventSink)
				return makeOutputSolution(connection, (PrimitiveOutputDeviceKeyEventSink) connection.getSink());
			else
				throw new IllegalArgumentException("Cannot take Event-Sink of Type " + connection.getSink().getClass());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ConfigSolutionPrimitive> makeSourceSolutionVariants(final PrimitiveConnection connection) {
		if (connection.getSource() instanceof PrimitiveKeyEventSource)
			return makeKeyPairInputSolution(connection, (PrimitiveKeyEventSource) connection.getSource());
		else
			throw new IllegalArgumentException("Cannot take Event-Source of Type " + connection.getSource().getClass());
	}

}
