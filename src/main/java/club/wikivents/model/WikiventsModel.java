package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.http4j.SecureToken;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.StorageOption;
import org.kisst.pko4j.index.MultiIndex;
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
	public final UniqueIndex<Wikivent> eventUrlIndex = new UniqueIndex<>(Wikivent.class, true, evt -> evt.getUrlPart());

	public final OrderedIndex<Wikivent> allEvents    = new OrderedIndex<>(Wikivent.class, evt -> evt.dateKey()+evt.getKey());
	public final OrderedIndex<Wikivent> newestEvents = new OrderedIndex<>(Wikivent.class, evt -> evt.getKey());

	public final MultiIndex<Wikivent> eventTags = new MultiIndex<>(Wikivent.class, evt -> evt.tagNames());
	public final MultiIndex<User>  userTags  = new MultiIndex<>(User.class,  usr -> usr.tagNames());

	public final TagRepository tags = new TagRepository(this);
	public final TagRepository.EventListener eventTagListener = tags.new EventListener();
	public final TagRepository.UserListener  userTagListener  = tags.new UserListener();

	public final PkoTable<Group> groups = new PkoTable<>(this, Group.class);
	public final PkoTable<User>  users  = new PkoTable<>(this, User.class);
	public final PkoTable<Wikivent> events = new PkoTable<>(this, Wikivent.class);


	public Iterable<Wikivent> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Wikivent> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
	
	@Override public String getSalt(SecureToken key) { 
		User u=users.read(key.data); 
		if (u==null)
			return null;
		return u.passwordSalt;
	}
}
