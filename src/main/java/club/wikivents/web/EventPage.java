package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;

public class EventPage extends WikiventsPage {
	private final Template show;
	//private final Template edit;
	private final GenericForm form;
			
	public EventPage(WikiventsSite site) {
		super(site);
		show=createTemplate("event.show");
		//edit=createTemplate("event.edit");
		form = new GenericForm(site)
				.addField(Event.schema.title, "Titel")
				.addDateField(Event.schema.date, "Datum")
				.addField(Event.schema.min, "Minimum aantal deelnemers")
				.addField(Event.schema.max, "Maximum aantal deelnemers")
				.addField(Event.schema.location, "Plaats")
				.addTextAreaField(Event.schema.description, "Omschrijving",10)
				;
	}
	@Override public String getPath() { return "event/*"; }

	@Override public void handleGet(String path, HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		if (path==null || path.equals("") || path.equals("new")) {
			Struct input=new HttpRequestStruct(request);
			// GET a form to create new Event
			form.output(data, input, response);
		}
		else {
			// GET the data for a existing Event
			Event event = model.events.read(path);
			data.add("event", event);
			show.output(data, response);
		}
	}
	@Override public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {
		//Data data = new Data()
		//		.add("user", getUser(request))
		//		.add("model", model);
		Struct input=new HttpRequestStruct(request);
		//Data data = createTemplateData(request);
		if (path==null || path.equals("") || path.equals("new")) {
			Event event=new Event(model,input);
			model.events.create(event);
			redirect(response,"event/"+event._id);
		}
		else {
			Event oldEvent=new Event(model,input); // TODO: encode oldEvent
			Event event=new Event(model,input);
			model.events.update(oldEvent, event);
			redirect(response,"event/"+event._id);
		}
		//String message="Unknown user";
		
	}

}
