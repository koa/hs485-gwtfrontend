/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import org.vectomatic.dom.svg.ui.ExternalSVGResource;

import com.google.gwt.resources.client.ClientBundle;

/**
 *
 */
public interface Resources extends ClientBundle {

	@Source("symbols/bulb_off.svg")
	ExternalSVGResource bulb_off();

	@Source("symbols/bulb_on.svg")
	ExternalSVGResource bulb_on();

}
