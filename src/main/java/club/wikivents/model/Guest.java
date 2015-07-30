package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudRef;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Guest extends ReflectStruct {
	public final CrudRef<User> user;
	public final Instant date;
	public Guest(WikiventsModel model, Struct data) {
		this.user=schema.user.getRef(model.users ,data);
		this.date = schema.date.getInstantOrNow(data);
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Comment> {
		public Schema() { super(Comment.class); addAllFields(); }
		public final RefField<User> user = new RefField<User>("user");
		public final InstantField date = new InstantField("date"); 
	}}
