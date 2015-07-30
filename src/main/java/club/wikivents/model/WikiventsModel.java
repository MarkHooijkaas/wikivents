package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;


public class WikiventsModel extends CrudModel {
	public WikiventsModel(StorageOption ... storage){ super(storage); initModel(); }

	public final CrudTable<User>  users  = new CrudTable<User>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<Event>(this, Event.schema);


	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

}
