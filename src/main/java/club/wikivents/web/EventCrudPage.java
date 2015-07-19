package club.wikivents.web;

import org.kisst.crud4j.CrudPage;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.item4j.struct.HashStruct;

import club.wikivents.model.Event;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class EventCrudPage extends  CrudPage<Event> implements WikiventsPage {

	private WikiventsModel model;

	public EventCrudPage(WikiventsSite site) {
		super(site, site.model.events,
			new GenericForm(site)
				.addField(Event.schema.title, "Titel")
				.addDateField(Event.schema.date, "Datum")
				.addField(Event.schema.min, "Minimum aantal deelnemers")
				.addField(Event.schema.max, "Maximum aantal deelnemers")
				.addField(Event.schema.location, "Plaats")
				.addTextAreaField(Event.schema.description, "Omschrijving",10)
			);
		this.model=site.model;
	}
	@Override public WikiventsModel getModel() { return model;}

	@Override protected void validateCreate(String userid, HashStruct data) {
		User.Table.Ref user=model.users.createRef(userid);
		data.put(Event.schema.organizer.getName(),user);
	}
}
