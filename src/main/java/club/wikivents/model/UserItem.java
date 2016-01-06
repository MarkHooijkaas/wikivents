package club.wikivents.model;

import java.time.Instant;

import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.ReflectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel.MyObject;

public class UserItem extends ReflectStruct implements MyObject, AccessChecker<User> {
	public final User.Ref user;
	public final Instant date;
	public final String comment;
	
	public static final Schema schema=new Schema();
	public static final class Schema extends ReflectSchema<UserItem> {
		private Schema() { super(UserItem.class);}
		public final User.Ref.Field user = new User.Ref.Field("user");  
		public final InstantField date = new InstantField("date"); 
		public final StringField comment = new StringField("comment"); 
	}
	
	public UserItem(WikiventsModel model, Struct data) {
		this.user= schema.user.getRef(model, data);
		this.date = schema.date.getInstantOrNow(data);
		this.comment= schema.comment.getString(data);
	}
	public UserItem(WikiventsModel model, User u) { this(model, u, null); }
	public UserItem(WikiventsModel model, User u, String comment) {
		this.user=u.getRef();
		this.date=Instant.now();
		this.comment=comment;
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && this.user._id.equals(user._id); }
	@Override public boolean mayBeViewedBy(User user) { return user!=null; }
	
	public static ImmutableSequence.StringExpression key=(obj) -> { return ((UserItem) obj).user._id; };

}