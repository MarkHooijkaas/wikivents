package club.wikivents.model;

import org.kisst.crud4j.impl.MongoTable;

import com.mongodb.DB;

public class WikiventsMongoModel implements WikiventsModel {
	private final UserTable users;
	private final Event.Table events;
	private final Attendance.Table attendance;

	
	@Override public User.Table users() { return users; }
	@Override public Event.Table events() {return events; }
	@Override public Attendance.Table attendance() { return attendance; }

	private class UserTable extends MongoTable<User> implements User.Table {
		private UniqueIndex<User> usernameIndex;
		public UserTable(DB db) { 
			super(User.schema, db); 
			usernameIndex = useUniqueIndex(User.schema.name);
		}
		@Override public UniqueIndex<User> usernameIndex() { return usernameIndex; }
	}
	
	private class EventTable extends MongoTable<Event> implements Event.Table {
		private MultiIndex<Event> organizerIndex;
		private OrderedIndex<Event> dateIndex;
		public EventTable(DB db) { 
			super(Event.schema, db); 
			this.organizerIndex = useMultiIndex(Event.schema.organizer);
			this.dateIndex= useOrderedIndex(Event.schema.date);
		}
		@Override public MultiIndex<Event> organizerIndex() { return organizerIndex; }
		@Override public OrderedIndex<Event> dateIndex() { return dateIndex; }
	}
	private class AttendanceTable extends MongoTable<Attendance> implements Attendance.Table {
		private MultiIndex<Attendance> userIndex;
		private MultiIndex<Attendance> eventIndex;
		public AttendanceTable(DB db) { 
			super(Attendance.schema, db); 
			this.userIndex= useMultiIndex(Attendance.schema.user);
			this.eventIndex= useMultiIndex(Attendance.schema.event);
		}
		@Override public MultiIndex<Attendance> userIndex() { return userIndex; }
		@Override public MultiIndex<Attendance> eventIndex() { return eventIndex; }
	}

	public WikiventsMongoModel(DB db) {
		users = new UserTable(db); 
		events = new EventTable(db);
		attendance = new AttendanceTable(db);
		
	}
}
