package club.wikivents.model;

import org.kisst.crud4j.MongoStorage;

import com.mongodb.DB;

public class WikiventsMongoModel extends WikiventsModel {
	private final UserTable users;
	private final EventTable events;
	private final AttendanceTable attendance;

	public UserTable users() { return users; }
	public EventTable events() {return events; }
	public AttendanceTable attendance() { return attendance; }

	public WikiventsMongoModel(DB db) {
		users = new UserTable(new MongoStorage<User>(User.schema, db)); 
		events = new EventTable(new MongoStorage<Event>(Event.schema, db));
		attendance = new AttendanceTable(new MongoStorage<Attendance>(Attendance.schema, db));
	}
}
