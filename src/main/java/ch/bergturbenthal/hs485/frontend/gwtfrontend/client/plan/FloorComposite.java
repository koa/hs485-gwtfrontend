package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.OMSVGUseElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.SelectableIcon;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconEntry;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.PositionXY;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class FloorComposite extends Composite {
	private Floor																currentFloor;
	private Plan																currentPlan;
	private final Collection<FloorEventHandler>	eventHandlers	= new ArrayList<FloorEventHandler>();
	private final Runnable											fullRedrawRunnable;
	private final OMSVGDefsElement							iconDef;
	private final OMSVGGElement									iconG;
	private String															inputIconId;
	private final Map<OutputDeviceType, String>	outputIconIds	= new HashMap<OutputDeviceType, String>();
	private final OMSVGGElement									rootG;
	private float																scale;
	private final Set<SelectableIcon>						selectedIcons	= new HashSet<SelectableIcon>();
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
		svgRootElement.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(final MouseMoveEvent event) {
				for (final FloorEventHandler handler : eventHandlers)
					handler.onMouseMove(event);
			}
		});
		svgRootElement.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(final MouseOutEvent event) {
				for (final FloorEventHandler handler : eventHandlers)
					handler.onMouseOut(event);
			}
		});
		svgRootElement.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(final MouseUpEvent event) {
				for (final FloorEventHandler handler : eventHandlers)
					handler.onMouseUp(event);
			}
		});
		fullRedrawRunnable = new Runnable() {

			@Override
			public void run() {
				repaintAll();
			}
		};
	}

	public void addFloorEventHandler(final FloorEventHandler handler) {
		eventHandlers.add(handler);
		handler.setCurrentPlan(currentPlan);
		handler.setCurrentFloor(currentFloor);
		handler.setFullRedrawRunnable(fullRedrawRunnable);
	}

	public Floor getCurrentFloor() {
		return currentFloor;
	}

	public Plan getCurrentPlan() {
		return currentPlan;
	}

	public void redrawAllIcons() {
		while (true) {
			final OMNode firstChild = iconG.getFirstChild();
			if (firstChild == null)
				break;
			iconG.removeChild(firstChild);
		}
		// System.out.println(svgDrawing.getPixelUnitToMillimeterX());
		final float iconSize = currentFloor.getIconSize().floatValue();
		for (final InputDevice inputDevice : currentFloor.getInputDevices()) {
			final OMSVGUseElement currentIcon = OMSVGParser.currentDocument().createSVGUseElement();
			final OMSVGTransformList transformList = currentIcon.getTransform().getBaseVal();
			final OMSVGTransform scaleTransform = svgDrawing.createSVGTransform();
			final OMSVGTransform moveTransform = svgDrawing.createSVGTransform();
			scaleTransform.setScale(iconSize, iconSize);
			final Runnable iconUpdater = new Runnable() {

				@Override
				public void run() {
					final PositionXY position = inputDevice.getPosition();
					final StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append('#');
					stringBuilder.append(inputIconId);
					if (selectedIcons.contains(inputDevice))
						stringBuilder.append("-selected");
					currentIcon.getHref().setBaseVal(stringBuilder.toString());
					moveTransform.setTranslate(position.getX(), position.getY());
				}
			};
			iconUpdater.run();
			transformList.appendItem(moveTransform);
			transformList.appendItem(scaleTransform);
			iconG.appendChild(currentIcon);
			currentIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					for (final FloorEventHandler handler : eventHandlers)
						handler.onInputDeviceClick(event, inputDevice, scale, iconUpdater);
				}
			});
			currentIcon.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(final MouseDownEvent event) {
					for (final FloorEventHandler handler : eventHandlers)
						handler.onInputDeviceMouseDown(event, inputDevice, scale, iconUpdater);
				}
			});
		}
		for (final OutputDevice outputDevice : currentFloor.getOutputDevices()) {
			final OMSVGUseElement currentIcon = OMSVGParser.currentDocument().createSVGUseElement();
			final OMSVGTransformList transformList = currentIcon.getTransform().getBaseVal();
			final OMSVGTransform scaleTransform = svgDrawing.createSVGTransform();
			final OMSVGTransform moveTransform = svgDrawing.createSVGTransform();
			scaleTransform.setScale(iconSize, iconSize);
			final Runnable iconUpdater = new Runnable() {

				@Override
				public void run() {
					final PositionXY position = outputDevice.getPosition();
					final StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append('#');
					stringBuilder.append(outputIconIds.get(outputDevice.getType()));
					if (selectedIcons.contains(outputDevice))
						stringBuilder.append("-selected");
					currentIcon.getHref().setBaseVal(stringBuilder.toString());
					moveTransform.setTranslate(position.getX(), position.getY());
				}
			};
			iconUpdater.run();
			transformList.appendItem(moveTransform);
			transformList.appendItem(scaleTransform);
			iconG.appendChild(currentIcon);
			currentIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					event.getX();
					for (final FloorEventHandler handler : eventHandlers)
						handler.onOutputDeviceClick(event, outputDevice, scale, iconUpdater);
				}
			});
			currentIcon.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(final MouseDownEvent event) {
					for (final FloorEventHandler handler : eventHandlers)
						handler.onOutputDeviceMouseDown(event, outputDevice, scale, iconUpdater);
				}
			});
		}
	}

	public void repaintAll() {
		if (currentFloor != null && currentFloor.getDrawing() != null && currentFloor.getDrawing().getFileDataContent() != null)
			svgDrawing = OMSVGParser.parse(currentFloor.getDrawing().getFileDataContent());
		else
			svgDrawing = OMSVGParser.createDocument().createSVGSVGElement();

		// final OMSVGDocument currentDocument = OMSVGParser.currentDocument();

		// final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		// final Element scaleGElement = rootG.getElement();
		final OMNodeList<OMNode> childNodes = rootG.getChildNodes();
		for (final OMNode childNode : childNodes)
			if ("svg-floor-drawing-g".equals(((OMElement) childNode).getId()))
				rootG.removeChild(childNode);
		final OMSVGGElement svgDrawingG = moveSvgToG(svgDrawing);
		svgDrawingG.setId("svg-floor-drawing-g");
		rootG.appendChild(svgDrawingG);
		// scaleGElement.appendChild(moveSvgToG(svgDrawing).getElement());

		scaleToFit();
		redrawAllIcons();

	}

	public void setCurrentFloor(final Floor currentFloor) {
		this.currentFloor = currentFloor;
		for (final FloorEventHandler handler : eventHandlers)
			handler.setCurrentFloor(currentFloor);
		repaintAll();
	}

	public void setCurrentPlan(final Plan currentPlan) {
		this.currentPlan = currentPlan;
		for (final FloorEventHandler handler : eventHandlers)
			handler.setCurrentPlan(currentPlan);
		if (currentPlan == null)
			return;
		updateIcons();
		final List<Floor> floors = currentPlan.getFloors();
		if (floors.size() > 0)
			setCurrentFloor(floors.get(0));
	}

	public void setSelectedIcons(final Collection<SelectableIcon> icons) {
		selectedIcons.clear();
		selectedIcons.addAll(icons);
		redrawAllIcons();
	}

	private void addIcon(final String iconId, final FileData image) {
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
	}

	private void addSelectedIcon(final String iconId) {
		final OMSVGDocument document = OMSVGParser.currentDocument();
		final OMSVGGElement selectedIconG = document.createSVGGElement();
		final OMSVGRectElement selectedRect = document.createSVGRectElement(-0.55f, -0.55f, 1.1f, 1.1f, 0.1f, 0.1f);
		selectedRect.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, "#40d040");
		selectedIconG.appendChild(selectedRect);
		final OMSVGUseElement useElement = document.createSVGUseElement();
		useElement.getHref().setBaseVal('#' + iconId);
		selectedIconG.appendChild(useElement);
		selectedIconG.setId(iconId + "-selected");
		iconDef.appendChild(selectedIconG);
	}

	private void loadIconIfNeeded(final FileData image, final String iconId, final Set<String> loadedFiles) {
		if (!loadedFiles.contains(image.getFileName())) {
			addIcon(iconId, image);
			addSelectedIcon(iconId);
			loadedFiles.add(image.getFileName());
		}
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

	private void scaleToFit() {
		if (svgDrawing == null)
			return;
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
		scale = Math.min(yScale, xScale);
		scaleXForm.setScale(scale, scale);
		final float xOffset = (imageWidth - drawingWidth * scale) / 2;
		final float yOffset = (imageHeight - drawingHeight * scale) / 2;
		translateXForm.setTranslate(xOffset, yOffset);

	}

	private void updateIcons() {
		while (iconDef.hasChildNodes())
			iconDef.removeChild(iconDef.getFirstChild());
		final Set<String> loadedFiles = new HashSet<String>();
		final IconEntry inputIcon = currentPlan.getIconSet().getInputIcon();
		final FileData image = inputIcon.getImage();
		inputIconId = "icon-" + image.getFileName();
		loadIconIfNeeded(image, inputIconId, loadedFiles);
		for (final Entry<OutputDeviceType, IconEntry> iconEntry : currentPlan.getIconSet().getOutputIcons().entrySet()) {
			final FileData outputImage = iconEntry.getValue().getImage();
			final String iconId = "icon-" + outputImage.getFileName();
			loadIconIfNeeded(outputImage, iconId, loadedFiles);
			outputIconIds.put(iconEntry.getKey(), iconId);
		}
	}
}
