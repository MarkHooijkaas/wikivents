package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.http4j.SecureToken;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.StorageOption;
import org.kisst.pko4j.index.OrderedIndex;
import org.kisst.pko4j.index.UniqueIndex;

import club.wikivents.web.WikiventsSite;

public class WikiventsModel extends PkoModel implements SecureToken.SaltFactory {
	public final WikiventsSite site;

	public WikiventsModel(WikiventsSite site, StorageOption<?> ... storage){ 
		super(storage);
		this.site=site;
		initModel();
	}

	public final UniqueIndex<User> usernameIndex = new UniqueIndex<>(User.class, true, usr -> usr.username);
	public final UniqueIndex<User> emailIndex    = new UniqueIndex<>(User.class, true, usr -> usr.email);
	public final UniqueIndex<Group> groupUrlIndex = new UniqueIndex<>(Group.class, true, grp -> grp.urlName);
	public final UniqueIndex<Event> eventUrlIndex = new UniqueIndex<>(Event.class, true, evt -> evt.getUrlPart());

	public final OrderedIndex<Event> allEvents    = new OrderedIndex<>(Event.class, evt -> evt.dateKey()+evt.getKey());
	public final OrderedIndex<Event> newestEvents = new OrderedIndex<>(Event.class, evt -> evt.getKey());

	public final PkoTable<User>  users  = new PkoTable<>(this, User.class);
	public final PkoTable<Event> events = new PkoTable<>(this, Event.class);
	public final PkoTable<Group> groups = new PkoTable<>(this, Group.class);


	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
	
	@Override public String getSalt(SecureToken key) { 
		User u=users.read(key.data); 
		if (u==null)
			return null;
		return u.passwordSalt;
	}
}
