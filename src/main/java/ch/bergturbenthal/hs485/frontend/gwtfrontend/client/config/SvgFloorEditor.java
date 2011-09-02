/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.OMSVGUseElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.FileUploadDialog;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.Resources;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.svg.SVGProcessor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.PositionXY;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class SvgFloorEditor extends Composite {

	private static final class IconData {
		private OMSVGGElement	icon;
		private OMSVGRect			viewPort;

		public OMSVGGElement getIcon() {
			return icon;
		}

		public OMSVGRect getViewPort() {
			return viewPort;
		}

		public void setIcon(final OMSVGGElement icon) {
			this.icon = icon;
		}

		public void setViewPort(final OMSVGRect viewPort) {
			this.viewPort = viewPort;
		}

	}

	interface SvgFloorEditorUiBinder extends UiBinder<Widget, SvgFloorEditor> {
	}

	private static final String						BULB_OFF_ICON_ID	= "bulb_off_icon";

	private static final String						BULB_ON_ICON_ID		= "bulb_on_icon";

	private static SvgFloorEditorUiBinder	uiBinder					= GWT.create(SvgFloorEditorUiBinder.class);

	@UiField
	Button																addFileButton;

	private final ConfigServiceAsync			configService			= ConfigServiceAsync.Util.getInstance();
	private OMSVGUseElement								dragIcon;
	private PositionXY										dragPosition;
	private OMSVGRect											dragViewPort;
	private final Map<String, IconData>		iconTemplates			= new HashMap<String, IconData>();
	private final List<OutputDevice>			outputDevices			= new ArrayList<OutputDevice>();

	@UiField
	Button																removeFloorButton;
	private final Resources								resources					= GWT.create(Resources.class);

	@UiField
	ListBox																selectFileListBox;

	@UiField
	ListBox																selectFloorListBox;

	private OMSVGSVGElement								svg;

	@UiField
	HTMLPanel															svgPanel;

	public SvgFloorEditor() {
		initWidget(uiBinder.createAndBindUi(this));

		// Test-Data
		final OutputDevice outputDevice = new OutputDevice();
		outputDevice.setName("Wohnzimmer");
		outputDevice.setPosition(new PositionXY());
		outputDevice.getPosition().setX(300);
		outputDevice.getPosition().setX(150);
		outputDevices.add(outputDevice);

		reloadFileList();
		try {
			resources.bulb_on().getSvg(new ResourceCallback<SVGResource>() {

				public void onError(final ResourceException e) {
					// TODO Auto-generated method stub
				}

				public void onSuccess(final SVGResource resource) {
					appendIcon(resource.getSvg(), BULB_ON_ICON_ID);
				}
			});
			resources.bulb_off().getSvg(new ResourceCallback<SVGResource>() {

				public void onError(final ResourceException e) {
					// TODO Auto-generated method stub

				}

				public void onSuccess(final SVGResource resource) {
					appendIcon(resource.getSvg(), BULB_OFF_ICON_ID);
				}
			});
		} catch (final ResourceException e) {
			throw new RuntimeException("Cannot Load SVG", e);
		}
	}

	private void appendIcon(final OMSVGSVGElement svgDoc, final String iconId) {
		final IconData icon = new IconData();
		icon.setViewPort(svgDoc.getViewport());
		final OMSVGGElement bulbOn = moveSvgToG(svgDoc);
		bulbOn.setId(iconId);
		icon.setIcon(bulbOn);
		iconTemplates.put(iconId, icon);
	}

	private void drawSvg() {
		if (svg == null)
			return;
		svgPanel.clear();
		final OMSVGRect viewport = svg.getViewport();
		final float xScale = svgPanel.getOffsetWidth() / viewport.getWidth();
		final float yScale = svgPanel.getOffsetHeight() / viewport.getHeight();
		final float scale = Math.min(yScale, xScale);

		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();
		final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		final OMSVGGElement rootG = currentDocument.createSVGGElement();
		final Element scaleGElement = rootG.getElement();
		final Element backgroundGElement = backgroundG.getElement();
		final Element svgElement = svg.getElement();
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			backgroundGElement.appendChild(svgElement.removeChild(node));
		scaleGElement.appendChild(backgroundGElement);
		svgElement.appendChild(scaleGElement);

		final OMSVGDefsElement iconDef = currentDocument.createSVGDefsElement();
		for (final IconData icon : iconTemplates.values())
			iconDef.appendChild(icon.getIcon());
		rootG.appendChild(iconDef);

		final OMSVGGElement iconsGroup = currentDocument.createSVGGElement();

		rootG.appendChild(iconsGroup);

		final OMSVGGElement xformGroup = rootG;

		final OMSVGTransformList xformList = xformGroup.getTransform().getBaseVal();
		final OMSVGTransform xform = svg.createSVGTransform();
		xformList.appendItem(xform);
		xform.setScale(scale, scale);

		for (final OutputDevice device : outputDevices) {
			final OMSVGUseElement useIcon = currentDocument.createSVGUseElement();
			final IconData iconData = iconTemplates.get(BULB_ON_ICON_ID);
			useIcon.getHref().setBaseVal("#" + iconData.getIcon().getId());

			final OMSVGRect viewPort2 = iconData.getViewPort();
			final PositionXY position = device.getPosition();
			useIcon.getX().getBaseVal().setValue(position.getX() - viewPort2.getCenterX());
			useIcon.getY().getBaseVal().setValue(position.getY() - viewPort2.getCenterY());
			iconsGroup.appendChild(useIcon);
			useIcon.addMouseDownHandler(new MouseDownHandler() {

				public void onMouseDown(final MouseDownEvent event) {
					dragPosition = position;
					dragIcon = useIcon;
					dragViewPort = iconData.getViewPort();
					System.out.println(dragViewPort.getCenterX() + ":" + dragViewPort.getCenterY());
				}
			});
			useIcon.addMouseUpHandler(new MouseUpHandler() {

				public void onMouseUp(final MouseUpEvent event) {
					dragPosition = null;
					dragIcon = null;
				}
			});
		}

		final SVGImage image = new SVGImage(svg);
		image.addMouseUpHandler(new MouseUpHandler() {

			public void onMouseUp(final MouseUpEvent event) {
				dragPosition = null;
				dragIcon = null;
			}
		});
		svgPanel.add(image);
		image.addMouseMoveHandler(new MouseMoveHandler() {
			private int			absoluteLeft	= image.getAbsoluteLeft();
			private boolean	absoluteSet		= false;
			private int			absoluteTop		= image.getAbsoluteTop();

			public void onMouseMove(final MouseMoveEvent event) {
				if (!absoluteSet) {
					absoluteLeft = image.getAbsoluteLeft();
					absoluteTop = image.getAbsoluteTop();
					absoluteSet = true;
				}
				if (dragIcon == null || dragPosition == null)
					return;

				final int x = event.getClientX() - absoluteLeft;
				final int y = event.getClientY() - absoluteTop;
				dragPosition.setX(x / scale);
				dragPosition.setY(y / scale);
				dragIcon.getX().getBaseVal().setValue(dragPosition.getX() - dragViewPort.getCenterX());
				dragIcon.getY().getBaseVal().setValue(dragPosition.getY() - dragViewPort.getCenterY());
				// System.out.println(dragPosition.getX() + ":" + dragPosition.getY());
			}
		});

	}

	/**
	 * @param svg
	 * @return
	 */
	private OMSVGGElement moveSvgToG(final OMSVGSVGElement svg) {
		SVGProcessor.normalizeIds(svg);
		final OMSVGGElement newG = OMSVGParser.currentDocument().createSVGGElement();
		final Element svgElement = svg.getElement();
		final Element newGElement = newG.getElement();
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			newGElement.appendChild(svgElement.removeChild(node));
		return newG;
	}

	@UiHandler("addFileButton")
	void onAddFileButtonClick(final ClickEvent event) {
		final FileUploadDialog fileUploadDialog = new FileUploadDialog();
		fileUploadDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
				reloadFileList();
			}
		});
		fileUploadDialog.setModal(true);
		fileUploadDialog.show();
	}

	@UiHandler("selectFileListBox")
	void onSelectFileListBoxChange(final ChangeEvent event) {
		try {
			final int selectedIndex = selectFileListBox.getSelectedIndex();
			final String filename = selectFileListBox.getValue(selectedIndex);
			final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseURL() + "file?filename=" + filename);
			requestBuilder.sendRequest(null, new RequestCallback() {

				public void onError(final Request request, final Throwable exception) {
					// TODO Auto-generated method stub

				}

				public void onResponseReceived(final Request request, final Response response) {
					svg = OMSVGParser.parse(response.getText());
					SVGProcessor.normalizeIds(svg);
					drawSvg();
				}
			});
		} catch (final RequestException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	private void reloadFileList() {
		configService.listFilesByMimeType("image/svg+xml", new AsyncCallback<List<String>>() {

			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			public void onSuccess(final List<String> result) {
				selectFileListBox.clear();
				for (final String filename : result)
					selectFileListBox.addItem(filename);

			}
		});
	}
}
