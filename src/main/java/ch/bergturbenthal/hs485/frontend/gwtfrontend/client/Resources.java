/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import org.vectomatic.dom.svg.ui.ExternalSVGResource;
import org.vectomatic.dom.svg.ui.ExternalSVGResource.Validated;

import com.google.gwt.resources.client.ClientBundle;

/**
 *
 */
public interface Resources extends ClientBundle {

	@Source("symbols/bulb_off.svg")
	@Validated(validated = false)
	ExternalSVGResource bulb_off();

	@Source("symbols/bulb_on.svg")
	@Validated(validated = false)
	ExternalSVGResource bulb_on();

	@Source("symbols/fan.svg")
	@Validated(validated = false)
	ExternalSVGResource fan();

	@Source("symbols/glossy_flame.svg")
	@Validated(validated = false)
	ExternalSVGResource glossy_flame();

	@Source("symbols/switch_2.svg")
	@Validated(validated = false)
	ExternalSVGResource switch_on();

}
