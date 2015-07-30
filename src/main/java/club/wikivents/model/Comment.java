package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudRef;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudModelObject;
import org.kisst.item4j.struct.Struct;

public  class Comment extends CrudModelObject {
	public final CrudRef<User> user;
	public final Instant date;
	public final String comment;

	public Comment(User u, String comment) {
		super(schema);
		this.user=u.getRef();
		this.date=Instant.now();
		this.comment=comment;
	}

	public Comment(WikiventsModel model, Struct props) {
		super(schema);
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
