package club.wikivents.web;

import org.kisst.crud4j.CrudPage;
import org.kisst.http4j.handlebar.GenericForm;

import club.wikivents.model.Event;

public class EventCrudPage extends CrudPage<Event> {

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
	}

}
