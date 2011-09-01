/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.List;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.FileUploadDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
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

	interface SvgFloorEditorUiBinder extends UiBinder<Widget, SvgFloorEditor> {
	}

	private static SvgFloorEditorUiBinder	uiBinder			= GWT.create(SvgFloorEditorUiBinder.class);

	@UiField
	Button																addFileButton;
	private final ConfigServiceAsync			configService	= ConfigServiceAsync.Util.getInstance();
	@UiField
	Button																removeFloorButton;
	@UiField
	ListBox																selectFileListBox;
	@UiField
	ListBox																selectFloorListBox;
	private OMSVGSVGElement								svg;

	@UiField
	HTMLPanel															svgPanel;

	public SvgFloorEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		reloadFileList();
	}

	private void drawSvg() {
		svgPanel.clear();
		final OMSVGRect viewport = svg.getViewport();
		final float xScale = svgPanel.getOffsetWidth() / viewport.getWidth();
		final float yScale = svgPanel.getOffsetHeight() / viewport.getHeight();
		final float minScale = Math.min(yScale, xScale);

		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();
		final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		final OMSVGGElement rootG = currentDocument.createSVGGElement();
		final Element rootGElement = rootG.getElement();
		final Element backgroundGElement = backgroundG.getElement();
		final Element svgElement = svg.getElement();
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			backgroundGElement.appendChild(svgElement.removeChild(node));
		rootGElement.appendChild(backgroundGElement);
		svgElement.appendChild(rootGElement);

		final OMSVGGElement xformGroup = rootG;

		final OMSVGTransformList xformList = xformGroup.getTransform().getBaseVal();
		final OMSVGTransform xform = svg.createSVGTransform();
		xformList.appendItem(xform);
		xform.setScale(minScale, minScale);
		final SVGImage image = new SVGImage(svg);

		svgPanel.add(image);

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
