package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StorageOption;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.web.WikiventsSite;


public class WikiventsModel extends CrudModel {
	public final WikiventsSite site;
	public final String userProfileTemplate;

	public WikiventsModel(WikiventsSite site, StorageOption ... storage){ 
		super(storage);
		this.site=site;
		//TemplateEngine engine = new TemplateEngine(props);
		CompiledTemplate templ = site.defaultTheme.userProfileTemplate;
		this.userProfileTemplate=templ.toString(new TemplateData(this));
		initModel();
	}

	public final CrudTable<User>  users  = new CrudTable<>(this, User.schema);
	public final CrudTable<Event> events = new CrudTable<>(this, Event.schema);
	public final CrudTable<Page>  pages = new CrudTable<>(this, Page.schema);


	public final UniqueIndex<User> usernameIndex = getUniqueIndex(User.class, User.schema.username);
	public final UniqueIndex<User> emailIndex    = getUniqueIndex(User.class, User.schema.email);

	public final OrderedIndex<Event> allEvents    = getOrderedIndex(Event.class, Event.schema.date, Event.schema._id);
	public final OrderedIndex<Event> newestEvents = getOrderedIndex(Event.class, Event.schema._id);

	public final UniqueIndex<Page> pageIndex = getUniqueIndex(Page.class, Page.schema.name);

	
	public Iterable<Event> futureEvents() { return allEvents.tailList(LocalDate.now().toString());}
	public Iterable<Event> pastEvents() { return allEvents.headList(LocalDate.now().plusDays(1).toString());}
	public String getUserProfileTemplate() { return this.userProfileTemplate; }
}
