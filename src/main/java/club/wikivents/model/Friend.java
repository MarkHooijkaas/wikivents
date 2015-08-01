package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.struct.Struct;

public class Friend extends CrudModelObject {
	public final CrudRef<User> user;
	public final Instant since;
	
	public Friend(WikiventsModel model, Struct data) {
		super(schema);
		this.user=schema.user.getRef(model.users ,data);
		this.since = schema.since.getInstantOrNow(data);
	}
	public Friend(WikiventsModel model, User u) {
		super(schema);
		this.user=u.getRef();
		this.since=Instant.now();
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Friend> {
		public Schema() { super(Friend.class); addAllFields(); }
		public final RefField<User> user = new RefField<User>("user");
		public final InstantField since = new InstantField("since"); 
	}}
