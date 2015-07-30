package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;
import org.kisst.item4j.struct.Struct;


public class WikiventsModel extends CrudModel {
	public final CrudTable<User>  users  = new CrudTable<User>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<Event>(this, Event.schema);

	public WikiventsModel(StorageOption ... storage){ super(storage); initModel(); }

	/*
	@Override public <T> T construct(Class<?> cls, Struct data) {
		//System.out.println("creating "+ReflectionUtil.smartClassName(cls)+" from "+data);
		if (Event.class==cls)
			return cast(new Event(this, data));
		else if (User.class==cls)
			return cast(new User(this, data));
		else if (Comment.class == cls)
			return cast(new Comment(this,data));
		else if (Guest.class == cls)
			return cast(new Guest(this,data));
		//System.out.println("using normal factory");
		return basicFactory.construct(cls, data);
	}
	@Override public <T> T construct(Class<?> cls, String data) { return basicFactory.construct(cls, data);}
	 */
	
	public void close() {
		users.close();
		events.close();
	}
	
	public User findUsername(String username) {
		for (Struct u: users.findAll()) { 
			if (u.getString(username).equals(username)) // || u.email.equals(username))
				return new User(this,u);
		}
		return null;
	}

}
