package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventPage extends WikiventsPage {
	private final Template show;
	//private final Template edit;
	private final GenericForm form;
			
	public EventPage(WikiventsSite site) {
		super(site);
		show=createTemplate("wikivents/event.show");
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
		Struct input=new HttpRequestStruct(request);
		if (path==null || path.equals("") || path.equals("new")) {
			// GET a form to create new Event
			form.outputEdit(data, input, response);
		}
		if ( path.startsWith("edit/")) {
			path=path.substring(5);
			Event event=model.events.read(path);
			ensureUser(request,response,event.organizer._id);
			//System.out.println("Editing "+event);
			data.add("event", event);
			form.outputEdit(data, event, response);
		}
		else {
			// GET the data for a existing Event
			Event event = model.events.read(path);
			data.add("event", event);
			show.output(data, response);
		}
	}
	@Override public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {
		Struct input=new HttpRequestStruct(request);
		if (path==null || path.equals("") || path.equals("new")) {
			User u = ensureUser(request,response);
			HashStruct data = new HashStruct(input);
			data.put(Event.schema.organizer.getName(),u._id);
			Event event=new Event(model,data);
			model.events.create(event);
			redirect(response,"event/"+event._id);
		}
		else { 
			if ( path.startsWith("edit/")) 
				path=path.substring(5);
			Event oldEvent=model.events.read(path);
			User u = ensureUser(request,response,oldEvent.organizer._id);
			//Event newEvent=model.events.createObject(input);
			Event newEvent=new Event(model,new MultiStruct(input,oldEvent));
			
			System.out.println("Checking Authorization "+u+" for "+oldEvent);
			if (! u._id.equals(oldEvent.organizer._id))
				throw new RuntimeException("Not Authorized");
			model.events.update(oldEvent, newEvent);
			redirect(response,"/event/"+newEvent._id);
		}
		//String message="Unknown user";
		
	}

}
