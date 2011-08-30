package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-08-30T12:04:32.827+0200")
@StaticMetamodel(Floor.class)
public class Floor_ {
	public static volatile SingularAttribute<Floor, Integer> floorId;
	public static volatile SingularAttribute<Floor, String> name;
	public static volatile CollectionAttribute<Floor, Room> rooms;
}
