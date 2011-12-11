package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.OMSVGUseElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSetEntry;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.PositionXY;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class FloorComposite extends Composite {
	private Floor																currentFloor;
	private Plan																currentPlan;
	private final OMSVGDefsElement							iconDef;
	private final OMSVGGElement									iconG;
	private final Map<InputDeviceType, String>	inputIconIds	= new HashMap<InputDeviceType, String>();
	private final OMSVGGElement									rootG;
	private OMSVGSVGElement											svgDrawing;
	private final SVGImage											svgImage;

	public FloorComposite() {
		currentFloor = new Floor();
		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();
		final OMSVGSVGElement svgRootElement = currentDocument.createSVGSVGElement();
		rootG = currentDocument.createSVGGElement();
		iconDef = currentDocument.createSVGDefsElement();
		iconG = currentDocument.createSVGGElement();
		svgRootElement.appendChild(iconDef);
		rootG.appendChild(iconG);
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

	public Plan getCurrentPlan() {
		return currentPlan;
	}

	public void setCurrentFloor(final Floor currentFloor) {
		this.currentFloor = currentFloor;
		reloadDrawing();
	}

	public void setCurrentPlan(final Plan currentPlan) {
		this.currentPlan = currentPlan;
		if (currentPlan == null)
			return;
		System.out.println("Update icons");
		updateIcons();
		System.out.println("Updated");
		final List<Floor> floors = currentPlan.getFloors();
		if (floors.size() > 0)
			setCurrentFloor(floors.get(0));
		System.out.println("finished");
	}

	private OMSVGGElement moveSvgToG(final OMSVGSVGElement svg) {
		// SVGProcessor.normalizeIds(svg);
		final OMSVGGElement newG = OMSVGParser.currentDocument().createSVGGElement();
		final Element svgElement = svg.getElement();
		final Element newGElement = newG.getElement();
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			newGElement.appendChild(svgElement.removeChild(node));
		return newG;
	}

	private void placeIcons() {
		for (final OMNode child : iconG.getChildNodes())
			iconG.removeChild(child);
		System.out.println(svgDrawing.getPixelUnitToMillimeterX());
		for (final InputDevice inputDevice : currentFloor.getInputDevices()) {
			final PositionXY position = inputDevice.getPosition();
			final String switchId = inputIconIds.get(InputDeviceType.SWITCH);
			final OMSVGUseElement currentIcon = OMSVGParser.currentDocument().createSVGUseElement();
			currentIcon.getHref().setBaseVal('#' + switchId);
			final OMSVGTransformList transformList = currentIcon.getTransform().getBaseVal();
			final OMSVGTransform scaleTransform = svgDrawing.createSVGTransform();
			final OMSVGTransform moveTransform = svgDrawing.createSVGTransform();
			scaleTransform.setScale(currentFloor.getIconSize(), currentFloor.getIconSize());
			moveTransform.setTranslate(position.getX(), position.getY());
			transformList.appendItem(moveTransform);
			transformList.appendItem(scaleTransform);
			iconG.appendChild(currentIcon);
		}
	}

	private void reloadDrawing() {
		svgDrawing = OMSVGParser.parse(currentFloor.getDrawing().getFileDataContent());

		// final OMSVGDocument currentDocument = OMSVGParser.currentDocument();

		// final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		// final Element scaleGElement = rootG.getElement();
		rootG.appendChild(moveSvgToG(svgDrawing));
		// scaleGElement.appendChild(moveSvgToG(svgDrawing).getElement());

		scaleToFit();
		placeIcons();

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

	private void updateIcons() {
		for (final OMNode child : iconDef.getChildNodes())
			iconDef.removeChild(child);
		System.out.println(currentPlan.getIconSet());
		inputIconIds.clear();
		final Set<String> loadedFiles = new HashSet<String>();
		final Map<InputDeviceType, IconSetEntry> inputIcons = currentPlan.getIconSet().getInputIcons();
		for (final Entry<InputDeviceType, IconSetEntry> inputIconEntry : inputIcons.entrySet()) {
			final FileData image = inputIconEntry.getValue().getImage();
			final String iconId = "icon-" + image.getFileName();
			if (!loadedFiles.contains(image.getFileName())) {

				final OMSVGSVGElement iconDoc = OMSVGParser.parse(image.getFileDataContent());
				final OMSVGGElement iconG = moveSvgToG(iconDoc);
				final OMSVGTransform baseScaleTransform = iconDoc.createSVGTransform();
				final OMSVGTransform centerTransform = iconDoc.createSVGTransform();
				final OMSVGRect viewport = iconDoc.getViewport();

				centerTransform.setTranslate(-viewport.getCenterX(), -viewport.getCenterY());
				final OMSVGTransformList transformList = iconG.getTransform().getBaseVal();
				final float scaleX = 1 / viewport.getWidth();
				final float scaleY = 1 / viewport.getHeight();
				final float iconScale = Math.min(scaleX, scaleY);
				baseScaleTransform.setScale(iconScale, iconScale);
				transformList.appendItem(baseScaleTransform);
				transformList.appendItem(centerTransform);
				iconG.setId(iconId);

				iconDef.appendChild(iconG);
				loadedFiles.add(image.getFileName());
			}
			inputIconIds.put(inputIconEntry.getKey(), iconId);
		}
	}
}
