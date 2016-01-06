package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.http4j.SecureToken;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.StorageOption;
import org.kisst.pko4j.index.MemoryOrderedIndex;
import org.kisst.pko4j.index.MemoryUniqueIndex;

import club.wikivents.web.WikiventsSite;

public class WikiventsModel extends PkoModel implements SecureToken.SaltFactory {
	public final WikiventsSite site;

	public WikiventsModel(WikiventsSite site, StorageOption ... storage){ 
		super(storage);
		this.site=site;
		initModel();
	}

	public final UniqueIndex<User> usernameIndex = new MemoryUniqueIndex<>(User.schema, true, User.schema.username);
	public final UniqueIndex<User> emailIndex    = new MemoryUniqueIndex<>(User.schema, true, User.schema.email);

	public final OrderedIndex<Event> allEvents    = new MemoryOrderedIndex<>(Event.schema, false, Event.schema.date, Event.schema._id);
	public final OrderedIndex<Event> newestEvents = new MemoryOrderedIndex<>(Event.schema, false, Event.schema._id);

	public final PkoTable<WikiventsModel,User>  users  = new PkoTable<>(this, User.schema);
	public final PkoTable<WikiventsModel,Event> events = new PkoTable<>(this, Event.schema);
	public final PkoTable<WikiventsModel,Group> groups = new PkoTable<>(this, Group.schema);


	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
	
	@Override public String getSalt(SecureToken key) { 
		User u=users.read(key.data); 
		if (u==null)
			return null;
		return u.passwordSalt;
	}
}
