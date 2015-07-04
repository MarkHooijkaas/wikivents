package club.wikivents.model;


public interface WikiventsModel {
	public User.Table users();
	public Event.Table events();
	public Attendance.Table attendance();
}
