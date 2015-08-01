package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Guest extends ReflectStruct implements CrudModelObject {
	public final CrudRef<User> user;
	public final Instant date;
	
	public Guest(WikiventsModel model, Struct data) {
		this.user=model.users.createRef(data.getString("user"));
		this.date = data.getInstant("date",Instant.now());
	}
	public Guest(WikiventsModel model, User u) {
		this.user=u.getRef();
		this.date=Instant.now();
	}
}