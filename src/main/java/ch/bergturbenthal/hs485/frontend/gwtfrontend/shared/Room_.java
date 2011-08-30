package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "Dali", date = "2011-08-30T14:27:01.323+0200")
@StaticMetamodel(Room.class)
public class Room_ {
	public static volatile SingularAttribute<Room, Floor>						floor;
	public static volatile SingularAttribute<Room, String>					name;
	public static volatile CollectionAttribute<Room, OutputDevice>	outputDevices;
	public static volatile SingularAttribute<Room, Integer>					roomId;
}
