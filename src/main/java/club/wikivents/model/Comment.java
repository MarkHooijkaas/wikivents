package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public  class Comment extends ReflectStruct implements CrudModelObject{
	public final User.Ref user;
	public final Instant date;
	public final String comment;

	public static final Schema schema=new Schema();
	public static final class Schema extends ObjectSchema<Comment> {
		private Schema() { super(Comment.class); addAllFields();}
		public final Field<User.Ref> user = new Field<User.Ref>(User.Ref.type, "user");  
		public final InstantField date = new InstantField("date"); 
		public final StringField comment = new StringField("comment"); 
	}

	
	public Comment(User u, String comment) {
		this.user=new User.Ref(u.model, u._id);
		this.date=Instant.now();
		this.comment=comment;
	}

	public Comment(WikiventsModel model, Struct data) {
		this.user=new User.Ref(model, data.getString("user"));
		this.date = data.getInstant("date",Instant.now());
		this.comment=data.getString("comment");
	}
}
