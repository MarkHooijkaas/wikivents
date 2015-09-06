package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class EventHandler extends WikiventsActionHandler<Event> {
	public EventHandler(WikiventsSite site) { super(site, site.model.events);	}


	@NeedsNoAuthentication
	public void listAll(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.events); }
	@NeedsNoAuthentication
	public void listFuture(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.futureEvents()); }
	@NeedsNoAuthentication
	public void listPast(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.pastEvents()); }

	@NeedsNoAuthentication
	public void view(WikiventsCall call, Event event) {
		call.output(call.getTheme().eventShow, event);
	}
	public void viewEdit(WikiventsCall call, Event event) {
		new Form(call,event).showForm();
	}

	public void handleEdit(WikiventsCall call, Event oldRecord) {
		Form formdata = new Form(call,null);
		if (formdata.isValid()) 
			model.events.updateFields(oldRecord, formdata.record);
		formdata.handle();
	}
	public void viewCreate(WikiventsCall call) {
		new Form(call).showForm();
	}
	
	public void handleCreate(WikiventsCall call) {
		Form formdata = new Form(call);
		if (formdata.isValid()) {
			Event e=new Event(call.model,call.user,formdata.record);
			model.events.create(e);
			call.redirect("/event/:"+e._id);
		}
		else
			formdata.handle();
	}
	public void handleChangeField(WikiventsCall call, Event oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}

	
	@NeedsNoAuthorization
	public void handleAddComment(WikiventsCall call, Event event) {
		String text=call.request.getParameter("comment");
		event.addComment(model, call.user, text);
	}
	@NeedsNoAuthorization
	public void handleAddGuest(WikiventsCall call, Event event) {
		event.addGuest(model, call.user);
	}
	@NeedsNoAuthorization
	public void handleRemoveGuest(WikiventsCall call, Event event) {
		event.removeGuest(model,call.user);
	}
	public void handleAddOrganizer(WikiventsCall call, Event event) {
		String username=call.request.getParameter("newOrganizer");
		User newOrganizer=model.usernameIndex.get(username);
		if (newOrganizer==null)
			call.sendError(500, "no organizer");
		event.addOrganizer(model, newOrganizer);
	}
	public void handleRemoveOrganizer(WikiventsCall call, Event event) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no organizer");
		event.removeOrganizer(model,user);
	}

	
	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().eventEdit, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().eventEdit); }

		public final InputField organizer=new InputField("organizer", call.userid);
		public final InputField title = new InputField(Event.schema.title);
		public final InputField imageUrl = new InputField(Event.schema.imageUrl);
		public final InputField date= new InputField(Event.schema.date);
		public final InputField time = new InputField(Event.schema.time);
		public final InputField min = new InputField(Event.schema.min);
		public final InputField max = new InputField(Event.schema.max);
		public final InputField guestsAllowed = new InputField(Event.schema.guestsAllowed);
		public final InputField backupGuestsAllowed= new InputField(Event.schema.backupGuestsAllowed);
		public final InputField location = new InputField(Event.schema.location);
		public final InputField guestInfo = new InputField(Event.schema.guestInfo);
		public final InputField description= new InputField(Event.schema.description);
	}
}

