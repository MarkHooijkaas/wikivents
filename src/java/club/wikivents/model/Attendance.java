package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.item4j.struct.Struct;

public class Attendance extends CrudObject {
	public final CrudTable.Ref<User> user;
	public final CrudTable.Ref<Event> event;
	
	public Attendance(WikiventsModel model, Struct s) {
		super(schema,s);
		this.user  = schema.user.get(model.users(), s);
		this.event = schema.event.get(model.events(), s);
	}

	public static Schema schema=new Schema();
	public static class Schema extends CrudSchema<Attendance> {
		public Schema() { super(Attendance.class); addAllFields();}
		public final RefField<User> user  = new RefField<User>(Attendance.class,"user"); 
		public final RefField<Event> event = new RefField<Event>(Attendance.class,"event"); 
	}
	
	public interface Table extends CrudTable<Attendance> {
		public MultiIndex<Attendance>  userIndex();
		public MultiIndex<Attendance> eventIndex();
	}
}
