package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

public interface FloorEventHandler {

	/**
	 * Clicked an Input-Device
	 * 
	 * @param event
	 *          Click-Event
	 * @param inputDevice
	 *          Device
	 * @param scale
	 *          current Scale
	 * @param iconUpdater
	 *          updates if Device has changed
	 */
	void onInputDeviceClick(ClickEvent event, InputDevice inputDevice, float scale, Runnable iconUpdater);

	void onInputDeviceMouseDown(MouseDownEvent event, InputDevice inputDevice, float scale, Runnable iconUpdater);

	void onMouseMove(MouseMoveEvent event);

	void onMouseOut(MouseOutEvent event);

	void onMouseUp(MouseUpEvent event);

	void onOutputDeviceClick(ClickEvent event, OutputDevice outputDevice, float scale, Runnable iconUpdater);

	void onOutputDeviceMouseDown(MouseDownEvent event, OutputDevice outputDevice, float scale, Runnable iconUpdater);

}
