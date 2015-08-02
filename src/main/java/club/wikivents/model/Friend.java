package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Friend extends ReflectStruct implements CrudModelObject {
	public final User.Ref user;
	public final Instant since;
	
	public Friend(WikiventsModel model, Struct data) {
		this.user=new User.Ref(model, data.getString("user"));
		this.since = data.getInstant("since", Instant.now());
	}
	public Friend(WikiventsModel model, User u) {
		this.user=new User.Ref(model, u._id);
		this.since=Instant.now();
	}
	public String name() { return user.get().username; }
	//@Override public String toString() { return "Friend("+user._id+")"; }
}
