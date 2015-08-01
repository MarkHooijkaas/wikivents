package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventPage extends WikiventsPage {
	public EventPage(WikiventsSite site) { super(site);	}
	public final EventForm crud=new EventForm(site);

	public final HttpCallHandler list=new TemplatePage(site,"event/list", this::listAllEvents);
	public final HttpCallHandler show=new TemplatePage(site,"event/show", this::eventRecord);
	public final HttpCallHandler edit=crud::handleEdit;
	public final HttpCallHandler create=crud::handleCreate;
	public final HttpCallHandler addComment=this::handleAddComment;
	public final HttpCallHandler addGuest=this::handleAddGuest;
	public final HttpCallHandler addOrganizer=this::handleAddOrganizer;
	
	public final HttpCallDispatcher handler=new HttpCallDispatcher(this); 	
	@Override public void handle(HttpCall call, String subPath) { handler.handle(WikiventsCall.of(call, model), subPath); }
	
	public void listAllEvents(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.events.findAll()); }
	public void eventRecord(TemplateData data, HttpCall httpcall, String subPath) { data.add("record", model.events.read(subPath)); }

	public void handleAddComment(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (! call.isPost()) {
			call.invalidPage();
			return;
		}
		call.ensureUser();
		String text=call.request.getParameter("comment");
		Event event=model.events.read(subPath);
		event.addComment(model, call.user, text);
		call.redirect("../show/"+event._id);
	}
	public void handleAddGuest(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (! call.isPost()) {
			call.invalidPage();
			return;
		}
		call.ensureUser();
		Event event=model.events.read(subPath);
		event.addGuest(model, call.user);
		call.redirect("../show/"+event._id);
	}
	public void handleAddOrganizer(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (! call.isPost()) {
			call.invalidPage();
			return;
		}
		call.ensureUser();
		String username=call.request.getParameter("newOrganizer");
		System.out.println("User "+username);
		User newOrganizer=model.usernameIndex.get(username);
		if (newOrganizer==null)
			return; // TODO: message
		System.out.println("User "+newOrganizer);
		Event event=model.events.read(subPath);
		System.out.println("Event "+event);
		event.addOrganizer(model, newOrganizer);
		call.redirect("../show/"+event._id);
	}
	
}

