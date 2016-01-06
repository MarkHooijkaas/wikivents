package club.wikivents.model;

import java.time.LocalDate;

import club.wikivents.web.WikiventsSite;
import org.kisst.http4j.SecureToken;
import org.kisst.pko4j.PkoCommand;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.StorageOption;

public class WikiventsModel extends PkoModel implements SecureToken.SaltFactory {
	public final WikiventsSite site;

	public WikiventsModel(WikiventsSite site, StorageOption ... storage){ 
		super(storage);
		this.site=site;
		initModel();
	}

	public final PkoTable<WikiventsModel,User>  users  = new PkoTable<>(this, User.schema);
	public final PkoTable<WikiventsModel,Event> events = new PkoTable<>(this, Event.schema);
	public final PkoTable<WikiventsModel,Group> groups = new PkoTable<>(this, Group.schema);

	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

	public final OrderedIndex<Event> allEvents    = getOrderedIndex(Event.class, Event.schema.date, Event.schema._id);
	public final OrderedIndex<Event> newestEvents = getOrderedIndex(Event.class, Event.schema._id);

	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
	
	@Override public String getSalt(SecureToken key) { 
		User u=users.read(key.data); 
		if (u==null)
			return null;
		return u.passwordSalt;
	}
	
	public static abstract class Command<T extends PkoObject<WikiventsModel, T>> implements PkoCommand<T> {
		public final WikiventsModel model;
		public final T record;
		public Command(WikiventsModel model, T record) { this.model=model; this.record=record;}
		public boolean mayBeDoneBy(User user) { return user.isAdmin; }
		@Override public void otherActions(boolean rerun) {}
		@Override public T target() {return record;}
		@Override public String toString() {return this.getClass().getSimpleName()+"("+record._id+")";}
	}

}
