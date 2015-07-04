package club.wikivents.model;

import java.io.File;

import org.kisst.crud4j.impl.FileTable;

public class WikiventsFileModel implements WikiventsModel {
	private final UserTable users;
	private final Event.Table events;
	private final Attendance.Table attendance;

	
	@Override public User.Table users() { return users; }
	@Override public Event.Table events() {return events; }
	@Override public Attendance.Table attendance() { return attendance; }

	private class UserTable extends FileTable<User> implements User.Table {
		private UniqueIndex<User> usernameIndex;
		public UserTable(File maindir) { 
			super(User.schema, maindir); 
			usernameIndex = useUniqueIndex(User.schema.name);
		}
		@Override public UniqueIndex<User> usernameIndex() { return usernameIndex; }
	}
	
	private class EventTable extends FileTable<Event> implements Event.Table {
		private MultiIndex<Event> organizerIndex;
		private OrderedIndex<Event> dateIndex;
		public EventTable(File maindir) { 
			super(Event.schema, maindir); 
			this.organizerIndex = useMultiIndex(Event.schema.organizer);
			this.dateIndex= useOrderedIndex();
		}
		@Override public MultiIndex<Event> organizerIndex() { return organizerIndex; }
		@Override public OrderedIndex<Event> dateIndex() { return dateIndex; }
	}
	private class AttendanceTable extends FileTable<Attendance> implements Attendance.Table {
		private MultiIndex<Attendance> userIndex;
		private MultiIndex<Attendance> eventIndex;
		public AttendanceTable(File maindir) { 
			super(Attendance.schema, maindir); 
			this.userIndex= useMultiIndex(Attendance.schema.user);
			this.eventIndex= useMultiIndex(Attendance.schema.event);
		}
		@Override public MultiIndex<Attendance> userIndex() { return userIndex; }
		@Override public MultiIndex<Attendance> eventIndex() { return eventIndex; }
	}


	public WikiventsFileModel(String filename) { this(new File(filename)); }
	public WikiventsFileModel(File dir) {
		users = new UserTable(dir); 
		events = new EventTable(dir);
		attendance = new AttendanceTable(dir);
		
	}
}
