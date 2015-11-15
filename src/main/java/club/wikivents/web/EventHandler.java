package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;

import club.wikivents.model.Comment;
import club.wikivents.model.Event;
import club.wikivents.model.Group;
import club.wikivents.model.User;

public class EventHandler extends WikiventsActionHandler<Event> {
	public EventHandler(WikiventsSite site) { super(site, site.model.events);	}

	@Override protected Event findRecord(String id) {
		Event result=super.findRecord(id);
		if (result!=null)
			CallInfo.instance.get().data=result.title;
		return result;
	}


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
		Form formdata = new Form(call,(Struct) null);
		if (formdata.isValid()) 
			model.events.updateFields(oldRecord, formdata.record);
		formdata.handle();
	}
	public void viewCreate(WikiventsCall call) {
		new Form(call).showForm();
	}
	public void viewCreateIdea(WikiventsCall call) {
		new Form(call, call.getTheme().eventCreateIdea).showForm();
	}
	
	public void handleCreateIdea(WikiventsCall call) { handleCreate(call); }
	public void handleCreate(WikiventsCall call) {
		if (! call.user.trusted())
			throw new RuntimeException("User "+call.user.username+" not trusted to create event "+call.request.getParameter("title") );
		Form formdata = new Form(call);
		if (formdata.isValid()) {
			Event e=new Event(call.model,call.user,formdata.record);
			CallInfo.instance.get().data=e.title;
			model.events.create(e);
			call.redirect("/event/:"+e._id);
		}
		else
			formdata.handle();
	}
	public void handleDelete(WikiventsCall call, Event event) {
		if (call.user.isAdmin)
			table.delete(event);
	}

	public void handleChangeField(WikiventsCall call, Event oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		String logValue=value;
		if (logValue.length()>10)
			logValue=logValue.substring(0, 7)+"...";
		CallInfo.instance.get().action="handleChangeField "+field+" to "+logValue;
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
		String userId=call.request.getParameter("userId");
		if (call.user._id.equals(userId) || call.user.isAdmin)
			event.removeGuest(model,userId);
	}
	public void handleAddOrganizer(WikiventsCall call, Event event) {
		String username=call.request.getParameter("newOrganizer");
		User newOrganizer=model.usernameIndex.get(username);
		if (newOrganizer==null)
			call.sendError(500, "no organizer");
		else
			event.addOrganizer(model, newOrganizer);
	}
	public void handleRemoveOrganizer(WikiventsCall call, Event event) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no organizer");
		else
			event.removeOrganizer(model,user);
	}

	public void handleAddGroup(WikiventsCall call, Event event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else
			event.addGroup(model, gr);
	}
	public void handleRemoveGroup(WikiventsCall call, Event event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else
			event.removeGroup(model,gr);
	}

	
	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;

		event.addLike(model, call.user);
	}
	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		event.removeLike(model,call.user);
	}
	
	public void handleRemoveComment(WikiventsCall call, Event event) {
		String commentId=call.request.getParameter("commentId");
		if (event==null || commentId==null)
			return;
		Comment com=event.findComment(commentId);
		model.events.removeSequenceItem(event, Event.schema.comments, com);
	}

	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().eventEdit, data); }
		public Form(WikiventsCall call, CompiledTemplate theme) { super(call, theme); }
		public Form(WikiventsCall call) { this(call, call.getTheme().eventEdit); }

		public final InputField organizer=new InputField("organizer", call.userid);
		public final InputField title = new InputField(Event.schema.title);
		public final InputField imageUrl = new InputField(Event.schema.imageUrl);
		public final InputField date= new InputField(Event.schema.date);
		public final InputField time = new InputField(Event.schema.time);
		public final InputField endTime = new InputField(Event.schema.endTime);
		public final InputField max = new InputField(Event.schema.max);
		public final InputField guestsAllowed = new InputField(Event.schema.guestsAllowed);
		public final InputField backupGuestsAllowed= new InputField(Event.schema.backupGuestsAllowed);
		public final InputField city = new InputField(Event.schema.city);
		public final InputField location = new InputField(Event.schema.location);
		public final InputField cost = new InputField(Event.schema.cost);
		public final InputField guestInfo = new InputField(Event.schema.guestInfo);
		public final InputField description= new InputField(Event.schema.description);
		public final InputField idea= new InputField(Event.schema.idea);
	}
}

