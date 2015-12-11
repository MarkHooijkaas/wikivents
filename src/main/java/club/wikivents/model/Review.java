package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ReflectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Review extends ReflectStruct implements CrudModelObject, AccessChecker<User> {
	public enum Type {
		KNOWN(10,"");
		
		public final int karma;
		public final String description;
		Type(int karma, String description) {
			this.karma=karma;
			this.description=description;
		}
	}
	
	public final User.Ref user;
	public final Instant date;
	public final Type type;
	public final String comment;
	
	public static final Schema schema=new Schema();
	public static final class Schema extends ReflectSchema<Review> {
		private Schema() { super(Review.class);}
		public final User.Ref.Field user = new User.Ref.Field("user");  
		public final InstantField date = new InstantField("date"); 
		public final StringField type = new StringField("type"); 
		public final StringField comment = new StringField("comment"); 
	}

	
	public Review(WikiventsModel model, Struct data) {
		this.user= schema.user.getRef(model, data);
		this.date = schema.date.getInstantOrNow(data);
		this.type = Type.valueOf(schema.type.getString(data));
		this.comment= schema.comment.getString(data);
	}
	public Review(WikiventsModel model, User u, Type type, String comment) {
		this.user=new User.Ref(model, u._id);
		this.date=Instant.now();
		this.type=type;
		this.comment=comment;
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && this.user._id.equals(user._id); }
	@Override public boolean mayBeViewedBy(User user) { return user!=null; }
}