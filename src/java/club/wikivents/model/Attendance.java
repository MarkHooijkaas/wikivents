package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.CrudSchema;
import org.kisst.struct4j.Struct;

public class Attendance extends CrudObject {
	public final CrudTable<User>.Ref user;
	public final CrudTable<Event>.Ref event;
	
	public Attendance(WikiventsModel model, Struct s) {
		super(s);
		this.user  = schema.user.getRef(model.users(), s);
		this.event = schema.event.getRef(model.events(), s);
	}

	public static Schema schema=new Schema();
	public static class Schema extends CrudSchema<Attendance> {
		public Schema() { super(Attendance.class); }
		public final RefField<User> user  = new RefField<User>("user", false); 
		public final RefField<Event> event = new RefField<Event>("event", false); 
	}

}
