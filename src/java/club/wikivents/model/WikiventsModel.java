package club.wikivents.model;

import org.kisst.crud4j.CrudFileTable;
import org.kisst.struct4j.Struct;

public class WikiventsModel {
	public final UserTable users = new UserTable();
	public final EventTable events = new EventTable();
	public final AttendanceTable attendance = new AttendanceTable();

	public class UserTable extends CrudFileTable<User> {
		public UserTable() { super(User.schema ); }
		@Override public User create(Struct s) { return new User(s); }
	}
	public class EventTable extends CrudFileTable<Event> {
		public EventTable() { super(Event.schema); }
		@Override public Event create(Struct s) { return new Event(WikiventsModel.this, s); }
	}
	public class AttendanceTable extends CrudFileTable<Attendance> {
		public AttendanceTable() { super(Attendance.schema); }
		@Override public Attendance create(Struct s) { return new Attendance(WikiventsModel.this, s); }
	}
}
