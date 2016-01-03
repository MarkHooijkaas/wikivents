package club.wikivents.model;

import java.time.Instant;

import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ReflectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel;

public  class Comment extends ReflectStruct implements PkoModel.MyObject, AccessChecker<User>{
	public final User.Ref user;
	public final Instant date;
	public final String comment;
	//public final boolean hidden;

	public static final Schema schema=new Schema();
	public static final class Schema extends ReflectSchema<Comment> {
		private Schema() { super(Comment.class);}
		public final User.Ref.Field user = new User.Ref.Field("user");  
		public final InstantField date = new InstantField("date"); 
		public final StringField comment = new StringField("comment"); 
		//public final BooleanField hidden = new BooleanField("hidden"); 
	}
	public String id() { return user._id+date.toEpochMilli(); }
	
	public Comment(User u, String comment) {
		this.user=new User.Ref(u.model, u._id);
		this.date=Instant.now();
		this.comment=comment;
		//this.hidden=false;
	}

	public Comment(WikiventsModel model, Struct data) {
		this.user= schema.user.getRef(model, data);
		this.date = schema.date.getInstantOrNow(data);
		this.comment= schema.comment.getString(data);
		//this.hidden= schema.hidden.getBoolean(data,false);
	}

	@Override public boolean mayBeViewedBy(User user) { return user!=null; }
	@Override public boolean mayBeChangedBy(User user) { return user.isAdmin || user._id.equals(this.user._id); }
}
