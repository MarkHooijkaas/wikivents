package club.wikivents.model;

import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.Storage;
import org.kisst.struct4j.Struct;

public abstract class WikiventsModel {
	abstract public UserTable users();
	abstract public EventTable events();
	abstract public AttendanceTable attendance();


	public class UserTable extends CrudTable<User> {
		public UserTable(Storage<User> storage) { super(storage); }
		@Override public User createObject(Struct s) { return new User(s); }
	}
	public class EventTable extends CrudTable<Event> {
		public EventTable(Storage<Event> storage) { super(storage); }
		@Override public Event createObject(Struct s) { return new Event(WikiventsModel.this, s); }
	}
	public class AttendanceTable extends CrudTable<Attendance> {
		public AttendanceTable(Storage<Attendance> storage) { super(storage); }
		@Override public Attendance createObject(Struct s) { return new Attendance(WikiventsModel.this, s); }
	}
}
