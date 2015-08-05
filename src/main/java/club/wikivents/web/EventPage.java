package club.wikivents.web;

import org.kisst.http4j.GetDataHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventPage extends WikiventsPage {
	public EventPage(WikiventsSite site) { super(site);	}
	public final EventForm crud=new EventForm(site);

	public final HttpCallHandler list=new TemplatePage(site,"event.list", this::listFutureEvents);
	public final HttpCallHandler past=new TemplatePage(site,"event.list", this::listPastEvents);
	public final HttpCallHandler all=new TemplatePage(site,"event.list", this::listAllEvents);
	public final HttpCallHandler show=new TemplatePage(site,"event.show", this::eventRecord);
	public final HttpCallHandler edit=crud::handleEdit;
	public final HttpCallHandler create=crud::handleCreate;
	public final HttpCallHandler addComment=this::handleAddComment;
	public final HttpCallHandler addGuest=this::handleAddGuest;
	public final HttpCallHandler removeGuest=this::handleRemoveGuest;
	public final HttpCallHandler addOrganizer=this::handleAddOrganizer;
	public final HttpCallHandler pastEvents=new GetDataHandler(this::pastEvents);
	public final HttpCallHandler futureEvents=new GetDataHandler(this::futureEvents);
	public final HttpCallHandler allEvents=new GetDataHandler(this::allEvents);
	
	public final HttpCallDispatcher handler=new HttpCallDispatcher(this); 	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		// add data to call
		handler.handle(call, subPath);
	}
	
	public void listFutureEvents(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.futureEvents()); }
	public void listPastEvents(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.pastEvents()); }
	public void listAllEvents(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.allEvents); }
	public void eventRecord(TemplateData data, HttpCall httpcall, String subPath) { data.add("record", model.events.read(subPath)); }

	public Object pastEvents(HttpCall call) { return model.pastEvents(); }
	public Object futureEvents(HttpCall call) { return model.futureEvents(); }
	public Object allEvents(HttpCall call) { return model.allEvents; }
	

	
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
	public void handleRemoveGuest(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (! call.isPost()) {
			call.invalidPage();
			return;
		}
		call.ensureUser();
		Event event=model.events.read(subPath);
		event.removeGuest(model,call.user);
		call.redirect("/event/show/"+event._id);
	}
	public void handleAddOrganizer(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (! call.isPost()) {
			call.invalidPage();
			return;
		}
		call.ensureUser();
		String username=call.request.getParameter("newOrganizer");
		User newOrganizer=model.usernameIndex.get(username);
		if (newOrganizer==null)
			return; // TODO: message
		Event event=model.events.read(subPath);
		event.addOrganizer(model, newOrganizer);
		call.redirect("../show/"+event._id);
	}
	
}

