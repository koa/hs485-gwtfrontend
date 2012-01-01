package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

public interface LabelGenerator {
	String makeLabelForInputConnector(InputConnector inputConnector);

	String makeLabelForOutputDevice(OutputDevice outputDevice);
}
