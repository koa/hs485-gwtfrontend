package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveOutputDeviceKeyEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveSwitchingOutputDeviceValueEventSink;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;

public class IO127SolutionBuilder extends AbstractSolutionBuilder implements SolutionBuilder {

	public IO127SolutionBuilder(final Registry registry, final ScheduledExecutorService executorService,
			final Map<KeySensor, DistributingMessageHandler> messageHandlers) {
		super(registry, executorService, messageHandlers);
	}

	@Override
	public Collection<ConfigSolutionPrimitive> makeSinkSolutionVariants(final PrimitiveConnection connection) {
		try {
			if (connection.getSink() instanceof PrimitiveOutputDeviceKeyEventSink)
				return makeKeyOutputSolution(connection, (PrimitiveOutputDeviceKeyEventSink) connection.getSink());
			else if (connection.getSink() instanceof PrimitiveSwitchingOutputDeviceValueEventSink)
				return makeSwitchingValueSolution(connection, (PrimitiveSwitchingOutputDeviceValueEventSink) connection.getSink());
			else
				throw new IllegalArgumentException("Cannot take Event-Sink of Type " + connection.getSink().getClass());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ConfigSolutionPrimitive> makeSourceSolutionVariants(final PrimitiveConnection connection) {
		if (connection.getSource() instanceof PrimitiveKeyEventSource)
			return makeIndependentKeyInputSolution(connection, (PrimitiveKeyEventSource) connection.getSource());
		else
			throw new IllegalArgumentException("Cannot take Event-Source of Type " + connection.getSource().getClass());
	}

}
