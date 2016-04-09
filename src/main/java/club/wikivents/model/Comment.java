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

	public static final Schema schema=new Schema();
	public static final class Schema extends ReflectSchema<Comment> {
		private Schema() { super(Comment.class);}
		public final User.Ref.Field user = new User.Ref.Field("user");  
		public final InstantField date = new InstantField("date"); 
		public final StringField comment = new StringField("comment"); 
	}
	public String id() { return user.getKey()+date.toEpochMilli(); }
	
	public Comment(User u, String comment) {
		this.user=u.getRef();
		this.date=Instant.now();
		this.comment=comment;
	}

	public Comment(WikiventsModel model, Struct data) {
		this.user= schema.user.getRef(model, data);
		this.date = schema.date.getInstantOrNow(data);
		this.comment= schema.comment.getString(data);
	}
	
	@Override public String toString() {
		return "Comment("+user.getKey()+","+comment+")"; 
	}
	@Override public boolean mayBeViewedBy(User actor) { return actor!=null; }
	@Override public boolean mayBeChangedBy(User actor) { return actor.isAdmin || (this.user.refersTo(actor)); }
}
