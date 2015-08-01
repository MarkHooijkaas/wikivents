package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public  class Comment extends ReflectStruct implements CrudModelObject{
	public final CrudRef<User> user;
	public final Instant date;
	public final String comment;

	public Comment(User u, String comment) {
		this.user=u.getRef();
		this.date=Instant.now();
		this.comment=comment;
	}

	public Comment(WikiventsModel model, Struct data) {
		this.user=model.users.createRef(data.getString("user"));
		this.date = data.getInstant("date",Instant.now());
		this.comment=data.getString("comment");
	}
	}
