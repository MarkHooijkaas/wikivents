package club.wikivents.web;

import org.kisst.crud4j.HttpCrudHandler;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.item4j.struct.HashStruct;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventCrudPage extends WikiventsPage {
	private final GenericForm form;
	private final HttpCrudHandler<Event> handler;
	
	public EventCrudPage(WikiventsSite site) {
		super(site);
		this.form = new GenericForm(site.engine)
			.addField(Event.schema.title, "Titel")
			.addDateField(Event.schema.date, "Datum")
			.addField(Event.schema.min, "Minimum aantal deelnemers")
			.addField(Event.schema.max, "Maximum aantal deelnemers")
			.addField(Event.schema.location, "Plaats")
			.addTextAreaField(Event.schema.description, "Omschrijving",10)
		;
		this.handler=new HttpCrudHandler<Event>(model.events, form);
	}
	@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
	
	protected void validateCreate(String userid, HashStruct data) {
		User.Table.Ref user=model.users.createRef(userid);
		data.put(Event.schema.organizer.getName(),user);
	}
}
