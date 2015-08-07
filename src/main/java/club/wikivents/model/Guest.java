package club.wikivents.model;

import java.time.Instant;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ObjectSchema;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Guest extends ReflectStruct implements CrudModelObject, AccessChecker<User> {
	public final User.Ref user;
	public final Instant date;
	
	public static final Schema schema=new Schema();
	public static final class Schema extends ObjectSchema<Guest> {
		private Schema() { super(Guest.class); addAllFields();}
		public final Field<User.Ref> user = new Field<User.Ref>(User.Ref.type, "user");  
		public final InstantField date = new InstantField("date"); 
	}

	
	public Guest(WikiventsModel model, Struct data) {
		this.user=new User.Ref(model, data.getString("user"));
		this.date = data.getInstant("date",Instant.now());
	}
	public Guest(WikiventsModel model, User u) {
		this.user=new User.Ref(model, u._id);
		this.date=Instant.now();
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || this.user._id.equals(user._id)); }
	@Override public boolean mayBeViewedBy(User user) { return user!=null; }

}