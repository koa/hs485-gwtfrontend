package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

/**
 * Interface to represent the messages contained in resource bundle:
 * /home/akoenig
 * /git/gwtfrontend/src/main/resources/ch/bergturbenthal/hs485/frontend
 * /gwtfrontend/client/Messages.properties'.
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {

	/**
	 * Translated "add Floor".
	 * 
	 * @return translated "add Floor"
	 */
	@DefaultMessage("add Floor")
	@Key("addFloor")
	String addFloor();

	/**
	 * Translated "Add".
	 * 
	 * @return translated "Add"
	 */
	@DefaultMessage("Add")
	@Key("addOutputDeviceEnry")
	String addOutputDeviceEnry();

	/**
	 * Translated "add Room".
	 * 
	 * @return translated "add Room"
	 */
	@DefaultMessage("add Room")
	@Key("addRoom")
	String addRoom();

	/**
	 * Translated "New button".
	 * 
	 * @return translated "New button"
	 */
	@DefaultMessage("New button")
	@Key("btnNewButton_html")
	String btnNewButton_html();

	/**
	 * Translated "New button".
	 * 
	 * @return translated "New button"
	 */
	@DefaultMessage("New button")
	@Key("btnNewButton_html_1")
	String btnNewButton_html_1();

	/**
	 * Translated "Cancel".
	 * 
	 * @return translated "Cancel"
	 */
	@DefaultMessage("Cancel")
	@Key("cancelText")
	String cancelText();

	/**
	 * @return
	 */
	@DefaultMessage("edit")
	@Key("edit")
	String edit();

	/**
	 * Translated "Edit Floors".
	 * 
	 * @return translated "Edit Floors"
	 */
	@DefaultMessage("Edit Floors")
	@Key("editFloors")
	String editFloors();

	/**
	 * Translated "Edit Floors ...".
	 * 
	 * @return translated "Edit Floors ..."
	 */
	@DefaultMessage("Edit Floors ...")
	@Key("editFloorsItem")
	String editFloorsItem();

	/**
	 * Translated "Edit Rooms".
	 * 
	 * @return translated "Edit Rooms"
	 */
	@DefaultMessage("Edit Rooms")
	@Key("editRooms")
	String editRooms();

	/**
	 * Translated "Edit Rooms ...".
	 * 
	 * @return translated "Edit Rooms ..."
	 */
	@DefaultMessage("Edit Rooms ...")
	@Key("editRoomsItem")
	String editRoomsItem();

	/**
	 * Translated "Floor".
	 * 
	 * @return translated "Floor"
	 */
	@DefaultMessage("Floor")
	@Key("floor")
	String floor();

	/**
	 * Translated "File".
	 * 
	 * @return translated "File"
	 */
	@DefaultMessage("File")
	@Key("mntmFile_text")
	String mntmFile_text();

	/**
	 * Translated "Edit Files ...".
	 * 
	 * @return translated "Edit Files ..."
	 */
	@DefaultMessage("Edit Files ...")
	@Key("mntmNewItem_text_1")
	String mntmNewItem_text_1();

	/**
	 * Translated "Settings".
	 * 
	 * @return translated "Settings"
	 */
	@DefaultMessage("Settings")
	@Key("mntmNewMenu_text")
	String mntmNewMenu_text();

	/**
	 * Translated "Enter your name".
	 * 
	 * @return translated "Enter your name"
	 */
	@DefaultMessage("Enter your name")
	@Key("nameField")
	String nameField();

	/**
	 * Translated "new Floor".
	 * 
	 * @return translated "new Floor"
	 */
	@DefaultMessage("new Floor")
	@Key("newFloor")
	String newFloor();

	/**
	 * Translated "new Room".
	 * 
	 * @return translated "new Room"
	 */
	@DefaultMessage("new Room")
	@Key("newRoom")
	String newRoom();

	/**
	 * Translated "Ok".
	 * 
	 * @return translated "Ok"
	 */
	@DefaultMessage("Ok")
	@Key("okText")
	String okText();

	/**
	 * @param name
	 * @return
	 */
	@DefaultMessage("Are you sure to remove {0} ?")
	String removeDeviceQuestion(String name);

	/**
	 * Translated "Remove".
	 * 
	 * @return translated "Remove"
	 */
	@DefaultMessage("Remove")
	@Key("removeText")
	String removeText();

	/**
	 * Translated "Room".
	 * 
	 * @return translated "Room"
	 */
	@DefaultMessage("Room")
	@Key("room")
	String room();
	String htmlNewHtml_html();
}
