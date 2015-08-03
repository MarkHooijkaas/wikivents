package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;


public class WikiventsModel extends CrudModel {
	public WikiventsModel(StorageOption ... storage){ super(storage); initModel(); }

	public final CrudTable<User>  users  = new CrudTable<User>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<Event>(this, Event.schema);


	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

	public final OrderedIndex<Event> allEvents    = getOrderedIndex(Event.class);
	
	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
}
