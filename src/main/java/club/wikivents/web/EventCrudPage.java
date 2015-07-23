package club.wikivents.web;

import org.kisst.crud4j.HttpCrudHandler;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.HashStruct;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventCrudPage extends WikiventsPage {
	private final GenericForm form;
	private final HttpCrudHandler<Event> handler;

	public class Form extends GenericForm {
		public Form(TemplateEngine engine) { super(engine); addAllFields(); }
		public final TextField title = new TextField(Event.schema.title, "Titel");
		public final DateField date= new DateField(Event.schema.date, "Datum");
		public final TextField min = new TextField(Event.schema.min, "Minimum aantal deelnemers");
		public final TextField max = new TextField(Event.schema.max, "Maximum aantal deelnemers");
		public final TextField location = new TextField(Event.schema.location, "Plaats");
		public final TextAreaField description= new TextAreaField(Event.schema.description, "Omschrijving",10);
	}
	
	
	public EventCrudPage(WikiventsSite site) {
		super(site);
		this.form = new Form(site.engine);
		this.handler=new HttpCrudHandler<Event>(model.events, form);
	}
	@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
	
	protected void validateCreate(String userid, HashStruct data) {
		User.Table.Ref user=model.users.createRef(userid);
		data.put(Event.schema.organizer.getName(),user);
	}
}
