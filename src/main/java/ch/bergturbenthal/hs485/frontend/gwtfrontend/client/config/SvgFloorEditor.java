/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.OMSVGUseElement;
import org.vectomatic.dom.svg.ui.ExternalSVGResource;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.FileUploadDialog;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.Messages;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.Resources;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.PositionXY;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class SvgFloorEditor extends Composite {

	private static final class IconData {
		private OMSVGGElement	icon;

		public OMSVGGElement getIcon() {
			return icon;
		}

		public void setIcon(final OMSVGGElement icon) {
			this.icon = icon;
		}

	}

	interface SvgFloorEditorUiBinder extends UiBinder<Widget, SvgFloorEditor> {
	}

	private static final String						BULB_OFF_ICON_ID	= "bulb_off_icon";

	private static final String						BULB_ON_ICON_ID		= "bulb_on_icon";

	private static final String						FAN_ID						= "fan_icon";

	private static final String						GLOSSY_FLAME_ID		= "glossy_flame_icon";

	private static final String						SWITCH_ON_ICON_ID	= "switch_on_icon";

	private static SvgFloorEditorUiBinder	uiBinder					= GWT.create(SvgFloorEditorUiBinder.class);

	private int														absoluteLeft;

	private int														absoluteTop;
	@UiField
	Button																addFileButton;
	@UiField
	Button																addFloorButton;
	@UiField
	VerticalPanel													addInputDeviceButton;
	private final ConfigServiceAsync			configService			= ConfigServiceAsync.Util.getInstance();

	private Floor													currentFloor;
	@UiField
	Button																decScaleButton;

	private OMSVGUseElement								dragIcon;

	private PositionXY										dragPosition;

	private final List<Floor>							floors						= new ArrayList<Floor>();

	private OMSVGGElement									iconsGroup;
	private final Map<String, IconData>		iconTemplates			= new HashMap<String, IconData>();

	@UiField
	Button																incScaleButton;

	@UiField
	Button																inputDeviceButton;

	private final List<InputDevice>				inputDevices			= new ArrayList<InputDevice>();
	private final Messages								messages					= GWT.create(Messages.class);
	@UiField
	Button																outputDeviceButton;

	private final List<OutputDevice>			outputDevices			= new ArrayList<OutputDevice>();

	@UiField
	Button																removeFloorButton;
	private final Resources								resources					= GWT.create(Resources.class);
	private float													scale;
	@UiField
	ListBox																selectFileListBox;
	@UiField
	ListBox																selectFloorListBox;

	private OMSVGSVGElement								svg;
	@UiField
	HTMLPanel															svgPanel;

	public SvgFloorEditor() {
		initWidget(uiBinder.createAndBindUi(this));

		showFloor(null);
		reloadFileList();
		reloadFloorList();
		try {
			final Map<String, ExternalSVGResource> iconResourceMap = new HashMap<String, ExternalSVGResource>();
			iconResourceMap.put(BULB_ON_ICON_ID, resources.bulb_on());
			iconResourceMap.put(BULB_OFF_ICON_ID, resources.bulb_off());
			iconResourceMap.put(SWITCH_ON_ICON_ID, resources.switch_on());
			iconResourceMap.put(GLOSSY_FLAME_ID, resources.glossy_flame());
			iconResourceMap.put(FAN_ID, resources.fan());

			final Set<Entry<String, ExternalSVGResource>> entrySet = iconResourceMap.entrySet();
			for (final Entry<String, ExternalSVGResource> entry : entrySet)
				entry.getValue().getSvg(new ResourceCallback<SVGResource>() {

					@Override
					public void onError(final ResourceException e) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(final SVGResource resource) {
						appendIcon(resource.getSvg(), entry.getKey());
						// showFloor(currentFloor);
					}
				});
		} catch (final ResourceException e) {
			throw new RuntimeException("Cannot Load SVG", e);
		}
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				drawSvg();
			}
		});
	}

	private void appendIcon(final OMSVGSVGElement svgDoc, final String iconId) {
		final IconData icon = new IconData();
		// icon.setViewPort(svgDoc.getViewport());
		final OMSVGGElement svgGElem = moveSvgToG(svgDoc);
		final OMSVGTransform scaleTransform = svgDoc.createSVGTransform();
		final OMSVGTransform centerTransform = svgDoc.createSVGTransform();
		final OMSVGRect baseRect = svgDoc.getViewport();
		centerTransform.setTranslate(-baseRect.getCenterX(), -baseRect.getCenterY());
		final OMSVGTransformList transformList = svgGElem.getTransform().getBaseVal();
		transformList.appendItem(scaleTransform);
		transformList.appendItem(centerTransform);
		svgGElem.setId(iconId);
		icon.setIcon(svgGElem);
		// icon.setScaleTransform(scaleTransform);
		iconTemplates.put(iconId, icon);
	}

	public void deleteOutputDevice(final OutputDevice device) {
		outputDevices.remove(device);
		updateIcons();
	}

	private void drawSvg() {
		if (svg == null)
			return;
		svgPanel.clear();
		final OMSVGRect viewport = svg.getViewport();
		final float xScale = svgPanel.getOffsetWidth() / viewport.getWidth();
		final float yScale = svgPanel.getOffsetHeight() / viewport.getHeight();
		scale = Math.min(yScale, xScale);

		final OMSVGDocument currentDocument = OMSVGParser.currentDocument();
		final OMSVGSVGElement svgOut = currentDocument.createSVGSVGElement();

		final OMSVGGElement backgroundG = currentDocument.createSVGGElement();
		final OMSVGGElement rootG = currentDocument.createSVGGElement();
		final Element scaleGElement = rootG.getElement();
		final Element backgroundGElement = backgroundG.getElement();
		final Element svgElement = (Element) svg.getElement().cloneNode(true);
		Node node;
		while ((node = svgElement.getFirstChild()) != null)
			backgroundGElement.appendChild(svgElement.removeChild(node));
		scaleGElement.appendChild(backgroundGElement);
		svgOut.appendChild(rootG);
		// svgElement.appendChild(scaleGElement);

		final OMSVGDefsElement iconDef = currentDocument.createSVGDefsElement();
		for (final IconData icon : iconTemplates.values())
			iconDef.appendChild(icon.getIcon());
		rootG.appendChild(iconDef);

		iconsGroup = currentDocument.createSVGGElement();

		rootG.appendChild(iconsGroup);

		final OMSVGGElement xformGroup = rootG;

		final OMSVGTransformList xformList = xformGroup.getTransform().getBaseVal();
		final OMSVGTransform xform = svgOut.createSVGTransform();
		xformList.appendItem(xform);
		xform.setScale(scale, scale);
		updateIcons();

		final SVGImage image = new SVGImage(svgOut);
		// image.getElement().setAttribute("oncontextmenu", "return false;");
		// image.addMouseUpHandler(new MouseUpHandler() {
		//
		// public void onMouseUp(final MouseUpEvent event) {
		// dragPosition = null;
		// dragIcon = null;
		// }
		// });
		svgPanel.add(image);
		image.addMouseMoveHandler(new MouseMoveHandler() {

			public void onMouseMove(final MouseMoveEvent event) {
				if (dragIcon == null || dragPosition == null)
					return;

				final int x = event.getClientX() - absoluteLeft;
				final int y = event.getClientY() - absoluteTop;
				dragPosition.setX(x / scale);
				dragPosition.setY(y / scale);
				dragIcon.getX().getBaseVal().setValue(dragPosition.getX());
				dragIcon.getY().getBaseVal().setValue(dragPosition.getY());
				// System.out.println(dragPosition.getX() + ":" + dragPosition.getY());
			}
		});

	}

	/**
	 * @param device
	 * @return
	 */
	private PopupPanel makePopupPanelForInputDevice(final InputDevice device) {
		final PopupPanel popupPanel = new PopupPanel(true);
		final MenuBar menuBar = new MenuBar(true);
		popupPanel.add(menuBar);
		menuBar.addItem("edit ...", new Command() {
			@Override
			public void execute() {
				popupPanel.hide();
				new EditInputDevice(device, new Runnable() {
					@Override
					public void run() {
						// configService.updateInputDevices(Arrays.asList(new InputDevice[]
						// { device }), new AsyncCallback<Iterable<InputDevice>>() {
						//
						// @Override
						// public void onFailure(final Throwable caught) {
						// // TODO Auto-generated method stub
						// }
						//
						// @Override
						// public void onSuccess(final Iterable<InputDevice> result) {
						// updateIcons();
						// }
						// });
						//
					}
				}).center();

			}
		});
		menuBar.addItem(new MenuItem("remove", new Command() {
			@Override
			public void execute() {
				popupPanel.hide();
				// if (Window.confirm("Are you sure to remove " + device.getName() +
				// "?"))
				// configService.removeInputDevice(device, new AsyncCallback<Void>() {
				//
				// @Override
				// public void onFailure(final Throwable caught) {
				// updateIcons();
				// // TODO Auto-generated method stub
				// }
				//
				// @Override
				// public void onSuccess(final Void result) {
				// inputDevices.remove(device);
				// updateIcons();
				// }
				// });
			}
		}));
		popupPanel.getElement().setAttribute("oncontextmenu", "return false;");
		return popupPanel;
	}

	private PopupPanel makePopupPanelForOutputDevice(final OutputDevice device) {
		final PopupPanel popupPanel = new PopupPanel(true);
		final MenuBar menuBar = new MenuBar(true);
		popupPanel.add(menuBar);
		menuBar.addItem(new MenuItem(messages.edit() + " ...", new Command() {

			@Override
			public void execute() {
				popupPanel.hide();
				new EditOutputDevice(device, new Runnable() {
					@Override
					public void run() {
						// configService.updateOutputDevices(Arrays.asList(new
						// OutputDevice[] { device }), new
						// AsyncCallback<Iterable<OutputDevice>>() {
						//
						// @Override
						// public void onFailure(final Throwable caught) {
						// // TODO Auto-generated method stub
						// }
						//
						// @Override
						// public void onSuccess(final Iterable<OutputDevice> result) {
						// updateIcons();
						// }
						// });

					}
				}).center();
			}
		}));
		menuBar.addItem(new MenuItem(messages.removeText(), new Command() {
			public void execute() {
				popupPanel.hide();
				// if (Window.confirm(messages.removeDeviceQuestion(device.getName())))
				// "Are you sure to remove " + device.getName() + "?"))
				// configService.removeOutputDevice(device, new AsyncCallback<Void>() {
				//
				// @Override
				// public void onFailure(final Throwable caught) {
				// updateIcons();
				// // TODO Auto-generated method stub
				// }
				//
				// @Override
				// public void onSuccess(final Void result) {
				// outputDevices.remove(device);
				// updateIcons();
				// }
				// });
			}
		}));
		popupPanel.getElement().setAttribute("oncontextmenu", "return false;");
		return popupPanel;
	}

	/**
	 * @param svg
	 * @return
	 */
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

	@UiHandler("addFileButton")
	void onAddFileButtonClick(final ClickEvent event) {
		final FileUploadDialog fileUploadDialog = new FileUploadDialog();
		fileUploadDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
				reloadFileList();
			}
		});
		fileUploadDialog.setModal(true);
		fileUploadDialog.center();
	}

	@UiHandler("addFloorButton")
	void onAddFloorButtonClick(final ClickEvent event) {
		final Floor newFloor = new Floor();
		newFloor.setName("Floor " + floors.size());
		// configService.updateFloors(Arrays.asList(new Floor[] { newFloor }), new
		// AsyncCallback<Void>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Void result) {
		// reloadFloorList();
		// }
		// });
	}

	@UiHandler("decScaleButton")
	void onDecScaleButtonClick(final ClickEvent event) {
		float iconScale = currentFloor.getScale();
		iconScale = iconScale * 0.8f;
		scaleIcons(iconScale);
		currentFloor.setScale(iconScale);
		saveFloor();
	}

	@UiHandler("incScaleButton")
	void onIncScaleButtonClick(final ClickEvent event) {
		float iconScale = currentFloor.getScale();
		iconScale = iconScale * 1.25f;
		scaleIcons(iconScale);
		currentFloor.setScale(iconScale);
		saveFloor();
	}

	@UiHandler("inputDeviceButton")
	void onInputDeviceButtonClick(final ClickEvent event) {
		final InputDevice inputDevice = new InputDevice();
		inputDevice.setName("Switch " + inputDevices.size());
		// TODO reimplement
		// inputDevice.getFloorPlace().getPosition().setX(300f);
		// inputDevice.getFloorPlace().getPosition().setY(300f);
		// inputDevice.getFloorPlace().setFloor(currentFloor);
		inputDevice.setType(InputDeviceType.SWITCH);
		inputDevices.add(inputDevice);
		updateInputDevicesOnServer();

	}

	@UiHandler("outputDeviceButton")
	void onOutputDeviceButtonClick(final ClickEvent event) {
		final OutputDevice outputDevice = new OutputDevice();
		outputDevice.setName("Lamp " + outputDevices.size());
		outputDevice.getFloorPlace().getPosition().setX(300f);
		outputDevice.getFloorPlace().getPosition().setY(300f);
		outputDevice.getFloorPlace().setFloor(currentFloor);
		outputDevice.setType(OutputDeviceType.LIGHT);
		outputDevices.add(outputDevice);
		updateOutputDevicesOnServer();
	}

	@UiHandler("selectFileListBox")
	void onSelectFileListBoxChange(final ChangeEvent event) {
		final int selectedIndex = selectFileListBox.getSelectedIndex();
		if (selectedIndex < 1)
			currentFloor.setPlan(null);
		else {
			final String filename = selectFileListBox.getValue(selectedIndex);
			configService.getFile(filename, new AsyncCallback<FileData>() {

				@Override
				public void onFailure(final Throwable caught) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(final FileData result) {
					currentFloor.setPlan(result);
					saveFloor();
					showFloor(currentFloor);
				}
			});
		}
	}

	@UiHandler("selectFloorListBox")
	void onSelectFloorListBoxChange(final ChangeEvent event) {
		final int selectedIndex = selectFloorListBox.getSelectedIndex();
		System.out.println(selectedIndex);
		if (selectedIndex < 0 || selectedIndex >= floors.size())
			showFloor(null);
		else {
			System.out.println(floors.get(selectedIndex));
			showFloor(floors.get(selectedIndex));
		}
	}

	/**
	 * @param iconId
	 * @param position
	 * @param popupPanel
	 */
	private void placeIconAt(final String iconId, final PositionXY position, final PopupPanel popupPanel) {
		final OMSVGUseElement useIcon = OMSVGParser.currentDocument().createSVGUseElement();
		final IconData iconData = iconTemplates.get(iconId);
		useIcon.getHref().setBaseVal("#" + iconData.getIcon().getId());

		// final OMSVGRect viewPort2 = iconData.getViewPort();
		useIcon.getX().getBaseVal().setValue(position.getX());
		useIcon.getY().getBaseVal().setValue(position.getY());
		iconsGroup.appendChild(useIcon);
		useIcon.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(final MouseDownEvent event) {
				final int mouseButton = event.getNativeButton();
				switch (mouseButton) {
				case NativeEvent.BUTTON_LEFT:
					dragPosition = position;
					dragIcon = useIcon;
					final int clientX = event.getClientX();
					final int clientY = event.getClientY();
					absoluteLeft = Math.round(clientX - dragPosition.getX() * scale);
					absoluteTop = Math.round(clientY - dragPosition.getY() * scale);
					break;
				case NativeEvent.BUTTON_RIGHT:
					popupPanel.setPopupPosition(event.getClientX(), event.getClientY());
					popupPanel.show();
					event.stopPropagation();
					event.preventDefault();
					break;

				default:
					break;
				}
			}
		});
		useIcon.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				System.out.println("Clicked: " + event.getNativeButton());
			}
		});
		useIcon.addMouseUpHandler(new MouseUpHandler() {

			public void onMouseUp(final MouseUpEvent event) {
				if (dragIcon != null) {
					updateOutputDevicesOnServer();
					updateInputDevicesOnServer();
				}
				dragPosition = null;
				dragIcon = null;
			}
		});
	}

	/**
	 * 
	 */
	private void reloadFileList() {
		// configService.listFilesByMimeType("image/svg+xml", new
		// AsyncCallback<List<String>>() {
		//
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void onSuccess(final List<String> result) {
		// selectFileListBox.clear();
		// selectFileListBox.addItem("----");
		// for (final String filename : result)
		// selectFileListBox.addItem(filename);
		// }
		// });
	}

	private void reloadFloorList() {
		// configService.listAllFloors(new AsyncCallback<Iterable<Floor>>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Iterable<Floor> result) {
		// selectFloorListBox.clear();
		// floors = new ArrayList<Floor>();
		// for (final Floor floor : result) {
		// floors.add(floor);
		// selectFloorListBox.addItem(floor.getName());
		// }
		// if (currentFloor != null)
		// for (int i = 0; i < floors.size(); i++)
		// if (currentFloor.getName().equals(floors.get(i).getName())) {
		// selectFloorListBox.setSelectedIndex(i);
		// showFloor(floors.get(i));
		// return;
		// }
		// if (floors.size() > 0)
		// showFloor(floors.get(0));
		// }
		// });
		// TODO Auto-generated method stub

	}

	private void removeAllChildren(final Element element) {
		Node node;
		while ((node = element.getFirstChild()) != null)
			element.removeChild(node);
	}

	/**
	 * 
	 */
	private void saveFloor() {
		// configService.updateFloors(Arrays.asList(new Floor[] { currentFloor }),
		// new AsyncCallback<Void>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Void result) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	private void scaleIcons(final float scaleFactor) {
		for (final IconData iconData : iconTemplates.values()) {
			final OMSVGTransform scaleTransform = iconData.getIcon().getTransform().getBaseVal().getItem(0);
			scaleTransform.setScale(scaleFactor, scaleFactor);
		}
	}

	private void showFloor(final Floor floor) {
		currentFloor = floor;
		addFileButton.setEnabled(currentFloor != null);
		removeFloorButton.setEnabled(currentFloor != null);
		selectFileListBox.setEnabled(currentFloor != null);
		outputDeviceButton.setEnabled(currentFloor != null);
		inputDeviceButton.setEnabled(currentFloor != null);
		if (currentFloor == null)
			return;
		if (currentFloor.getScale() == null || currentFloor.getScale().floatValue() == 0)
			currentFloor.setScale(1f);
		scaleIcons(currentFloor.getScale());
		final FileData plan = currentFloor.getPlan();
		if (plan == null)
			return;
		selectFileListBox.setSelectedIndex(0);
		for (int i = 0; i < selectFileListBox.getItemCount(); i++)
			if (currentFloor.getPlan().getFileName().equals(selectFileListBox.getValue(i)))
				selectFileListBox.setSelectedIndex(i);

		// configService.getOutputDevicesByFloor(currentFloor, new
		// AsyncCallback<Iterable<OutputDevice>>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Iterable<OutputDevice> result) {
		// outputDevices.clear();
		// for (final OutputDevice outputDevice : result)
		// outputDevices.add(outputDevice);
		// updateIcons();
		// }
		// });
		// configService.getInputDevicesByFloor(currentFloor, new
		// AsyncCallback<Iterable<InputDevice>>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Iterable<InputDevice> result) {
		// inputDevices.clear();
		// for (final InputDevice inputDevice : result)
		// inputDevices.add(inputDevice);
		// updateIcons();
		// }
		// });
		svg = OMSVGParser.parse(plan.getFileDataContent());
		// SVGProcessor.normalizeIds(svg);
		drawSvg();

	}

	private void updateIcons() {
		if (iconsGroup == null)
			return;
		removeAllChildren(iconsGroup.getElement());
		// TODO readd
		// for (final InputDevice device : inputDevices)
		// placeIconAt(SWITCH_ON_ICON_ID, device.getFloorPlace().getPosition(),
		// makePopupPanelForInputDevice(device));
		for (final OutputDevice device : outputDevices) {
			final String iconId;
			if (device.getType() == null)
				iconId = BULB_OFF_ICON_ID;
			else
				switch (device.getType()) {
				case LIGHT:
					iconId = BULB_ON_ICON_ID;
					break;
				case HEAT:
					iconId = GLOSSY_FLAME_ID;
					break;
				case FAN:
					iconId = FAN_ID;
					break;
				default:
					iconId = BULB_OFF_ICON_ID;
					break;
				}
			placeIconAt(iconId, device.getFloorPlace().getPosition(), makePopupPanelForOutputDevice(device));
		}
	}

	/**
	 * 
	 */
	private void updateInputDevicesOnServer() {
		inputDeviceButton.setEnabled(false);
		// configService.updateInputDevices(inputDevices, new
		// AsyncCallback<Iterable<InputDevice>>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// inputDeviceButton.setEnabled(true);
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(final Iterable<InputDevice> result) {
		// inputDevices.clear();
		// for (final InputDevice inputDevice : result)
		// inputDevices.add(inputDevice);
		// updateIcons();
		// inputDeviceButton.setEnabled(true);
		// }
		// });
	}

	private void updateOutputDevicesOnServer() {
		outputDeviceButton.setEnabled(false);
		// configService.updateOutputDevices(outputDevices, new
		// AsyncCallback<Iterable<OutputDevice>>() {
		//
		// @Override
		// public void onFailure(final Throwable caught) {
		// outputDeviceButton.setEnabled(true);
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onSuccess(final Iterable<OutputDevice> result) {
		// outputDevices.clear();
		// for (final OutputDevice outputDevice : result)
		// outputDevices.add(outputDevice);
		// updateIcons();
		// outputDeviceButton.setEnabled(true);
		// }
		// });
	}
}
