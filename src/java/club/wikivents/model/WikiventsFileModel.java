package club.wikivents.model;

import java.io.File;

import org.kisst.crud4j.FileStorage;

public class WikiventsFileModel extends WikiventsModel {
	private final UserTable users;
	private final EventTable events;
	private final AttendanceTable attendance;

	public UserTable users() { return users; }
	public EventTable events() {return events; }
	public AttendanceTable attendance() { return attendance; }

	public WikiventsFileModel(String filename) { this(new File(filename)); }
	public WikiventsFileModel(File dir) {
		users = new UserTable(new FileStorage<User>(User.schema, dir)); 
		events = new EventTable(new FileStorage<Event>(Event.schema, dir));
		attendance = new AttendanceTable(new FileStorage<Attendance>(Attendance.schema, dir));
	}
}
