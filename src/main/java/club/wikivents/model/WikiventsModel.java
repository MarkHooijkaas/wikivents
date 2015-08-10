package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;

import club.wikivents.web.WikiventsSite;


public class WikiventsModel extends CrudModel {
	public final WikiventsSite site;

	public WikiventsModel(WikiventsSite site, StorageOption ... storage){ 
		super(storage);
		this.site=site;
		initModel(); 
	}

	public final CrudTable<User>  users  = new CrudTable<>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<>(this, Event.schema);
	public final CrudTable<Page>  pages = new CrudTable<>(this, Page.schema);


	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

	public final OrderedIndex<Event> allEvents    = getOrderedIndex(Event.class);

	public final UniqueIndex<Page> pageIndex = getUniqueIndex(Page.class, Page.schema.name);

	
	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
}
