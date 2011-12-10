package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class FloorComposite extends Composite {
	private Floor								currentFloor;
	private final OMSVGGElement	rootG;
	private OMSVGSVGElement			svgDrawing;
	private final SVGImage			svgImage;

	public FloorComposite() {
		currentFloor = new Floor();
		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();
		final OMSVGSVGElement svgRootElement = currentDocument.createSVGSVGElement();
		rootG = currentDocument.createSVGGElement();
		svgRootElement.appendChild(rootG);
		svgImage = new SVGImage(svgRootElement);
		initWidget(svgImage);
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(final ResizeEvent event) {
				scaleToFit();
			}
		});
	}

	public Floor getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(final Floor currentFloor) {
		this.currentFloor = currentFloor;
		reloadDrawing();
	}

	private void reloadDrawing() {
		svgDrawing = OMSVGParser.parse(currentFloor.getDrawing().getFileDataContent());

		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();

		final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		final Element scaleGElement = rootG.getElement();
		final Element backgroundGElement = backgroundG.getElement();
		final Element svgElement = (Element) svgDrawing.getElement().cloneNode(true);
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			backgroundGElement.appendChild(svgElement.removeChild(node));
		scaleGElement.appendChild(backgroundGElement);
		scaleToFit();
		// updateIcons();

	}

	private void scaleToFit() {
		final OMSVGTransformList xformList = rootG.getTransform().getBaseVal();
		xformList.clear();
		final OMSVGTransform scaleXForm = rootG.getOwnerSVGElement().createSVGTransform();
		final OMSVGTransform translateXForm = rootG.getOwnerSVGElement().createSVGTransform();
		xformList.appendItem(translateXForm);
		xformList.appendItem(scaleXForm);
		final OMSVGRect viewport = svgDrawing.getViewport();
		final int imageWidth = svgImage.getElement().getOffsetWidth();
		final int imageHeight = svgImage.getElement().getOffsetHeight();
		final float drawingWidth = viewport.getWidth();
		final float drawingHeight = viewport.getHeight();
		final float yScale = imageHeight / drawingHeight;
		final float xScale = imageWidth / drawingWidth;
		final float scale = Math.min(yScale, xScale);
		scaleXForm.setScale(scale, scale);
		translateXForm.setTranslate((imageWidth - drawingWidth * scale) / 2, (imageHeight - drawingHeight * scale) / 2);

	}

}
