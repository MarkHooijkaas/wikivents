package club.wikivents.model;

import java.time.Instant;

import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.ReflectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel.MyObject;

public class Guest extends ReflectStruct implements MyObject, AccessChecker<User> {
	public static ImmutableSequence.StringExpression key=(guest) -> {return ((Guest) guest).user.getKey(); };

	
	public final User.Ref user;
	public final Instant date;
	
	public static final Schema schema=new Schema();
	public static final class Schema extends ReflectSchema<Guest> {
		private Schema() { super(Guest.class);}
		public final User.Ref.Field user = new User.Ref.Field("user");  
		public final InstantField date = new InstantField("date"); 
	}

	
	public Guest(WikiventsModel model, Struct data) {
		this.user= schema.user.getRef(model, data);
		this.date = schema.date.getInstantOrNow(data);
	}
	public Guest(User u) {
		this.user=u.getRef();
		this.date=Instant.now();
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && this.user.refersTo(user); }
	@Override public boolean mayBeViewedBy(User user) { return user!=null; }
	@Override public String toString() { return "Guest("+user.name()+")"; }

}