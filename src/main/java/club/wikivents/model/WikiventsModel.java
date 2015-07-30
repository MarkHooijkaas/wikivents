package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;
import org.kisst.item4j.struct.Struct;


public class WikiventsModel extends CrudModel {
	public WikiventsModel(StorageOption ... storage){ super(storage); initModel(); }

	public final CrudTable<User>  users  = new CrudTable<User>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<Event>(this, Event.schema);


	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

	
	public User findUsername(String username) {
		for (Struct u: users.findAll()) { 
			if (u.getString(username).equals(username)) // || u.email.equals(username))
				return new User(this,u);
		}
		return null;
	}

}
