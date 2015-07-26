package club.wikivents.web;

import org.kisst.crud4j.HttpCrudDispatcher;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventCrudPage extends WikiventsPage {
	private final GenericForm form;
	private final HttpCrudDispatcher<Event> handler;

	public class Form extends GenericForm {
		public Form(TemplateEngine engine) { 
			super(engine,"event/event.",
					Event.schema.title,
					Event.schema.date,
					Event.schema.min,
					Event.schema.max,
					Event.schema.location,
					Event.schema.description);
		}
		@Override public void validateCreate(String userid, HashStruct data) {
			User.Table.Ref user=model.users.createRef(userid);
			if (user==null)
				throw new RuntimeException("no user");
			data.put(Event.schema.organizer.getName(),user);
		}
		@Override public void validateUpdate(String userid, Struct oldRecord, Struct newRecord) {}
	}
	
	
	public EventCrudPage(WikiventsSite site) {
		super(site);
		this.form = new Form(site.engine);
		this.handler=new HttpCrudDispatcher<Event>(model.events, form);
	}
	@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }	
}

