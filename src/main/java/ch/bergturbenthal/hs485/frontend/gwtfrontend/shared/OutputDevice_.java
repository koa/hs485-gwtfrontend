package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDevice.Type;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-08-30T12:06:54.068+0200")
@StaticMetamodel(OutputDevice.class)
public class OutputDevice_ {
	public static volatile SingularAttribute<OutputDevice, Integer> deviceId;
	public static volatile SingularAttribute<OutputDevice, String> name;
	public static volatile SingularAttribute<OutputDevice, Type> type;
	public static volatile SingularAttribute<OutputDevice, Room> room;
}
