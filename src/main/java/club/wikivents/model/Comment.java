package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudRef;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public  class Comment extends ReflectStruct {
	public final CrudRef<User> user;
	public final Instant date;
	public final String comment;

	public Comment(User u, String comment) {
		this.user=u.getRef();
		this.date=Instant.now();
		this.comment=comment;
	}

	public Comment(WikiventsModel model, Struct props) {
		this.user=schema.user.getRef(model.users ,props);
		this.date = schema.date.getInstantOrNow(props);
		this.comment=schema.comment.getString(props);
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Comment> {
		public Schema() { super(Comment.class); addAllFields(); }
		public final RefField<User> user = new RefField<User>("user");
		public final InstantField date = new InstantField("date"); 
		public final StringField comment = new StringField("comment"); 
	}

}
