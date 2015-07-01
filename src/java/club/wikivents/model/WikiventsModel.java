package club.wikivents.model;

import org.kisst.crud4j.CrudCachedTable;
import org.kisst.crud4j.Storage;
import org.kisst.struct4j.Struct;

public abstract class WikiventsModel {
	abstract public UserTable users();
	abstract public EventTable events();
	abstract public AttendanceTable attendance();


	public class UserTable extends CrudCachedTable<User> {
		public UserTable(Storage<User> storage) { super(storage); }
		@Override public User createObject(Struct s) { return new User(s); }
	}
	public class EventTable extends CrudCachedTable<Event> {
		public EventTable(Storage<Event> storage) { super(storage); }
		@Override public Event createObject(Struct s) { return new Event(WikiventsModel.this, s); }
	}
	public class AttendanceTable extends CrudCachedTable<Attendance> {
		public AttendanceTable(Storage<Attendance> storage) { super(storage); }
		@Override public Attendance createObject(Struct s) { return new Attendance(WikiventsModel.this, s); }
	}
}
