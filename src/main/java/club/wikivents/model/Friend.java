package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Friend extends ReflectStruct implements CrudModelObject {
	public final CrudRef<User> user;
	public final Instant since;
	
	public Friend(WikiventsModel model, Struct data) {
		this.user=model.users.createRef(data.getString("user"));
		this.since = data.getInstant("since", Instant.now());
	}
	public Friend(WikiventsModel model, User u) {
		this.user=u.getRef();
		this.since=Instant.now();
	}
	//@Override public String toString() { return "Friend("+user._id+")"; }
}
