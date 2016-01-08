package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.Comment;
import club.wikivents.model.Event;
import club.wikivents.model.EventCommands.AddGuestCommand;
import club.wikivents.model.EventCommands.RemoveGuestCommand;
import club.wikivents.model.Group;
import club.wikivents.model.Guest;
import club.wikivents.model.User;

public class EventHandler extends WikiventsActionHandler<Event> {
	public static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

	public final Event.Schema schema;


	public EventHandler(WikiventsSite site) { super(site, site.model.events); this.schema=Event.schema; }

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

	public AddGuestCommand createAddGuestCommand(WikiventsCall call, Event event) {
		return new AddGuestCommand(event, call.user);
	}
	public RemoveGuestCommand createRemoveGuestCommand(WikiventsCall call, Event event) {
		String guestId=call.request.getParameter("guest");
		Guest guest = event.findGuest(guestId);
		if (guest==null) {
			logger.warn("Could not find guest with id "+guestId);
			return null;
		}
		return new RemoveGuestCommand(event, guest.user.get());
	}
	
	public void handleEdit(WikiventsCall call, Event event) {
		Form formdata = new Form(call,(Struct) null);
		if (formdata.isValid()) 
			table.update(event, event.changeFields(formdata.record));
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
			table.create(e);
			call.redirect("/event/:"+e._id);
		}
		else
			formdata.handle();
	}
	public void handleDelete(WikiventsCall call, Event event) {
		if (call.user.isAdmin)
			table.delete(event);
	}

	
	@NeedsNoAuthorization
	public void handleAddComment(WikiventsCall call, Event event) {
		String text=call.request.getParameter("comment");
		if (call.user.mayComment())
			table.update(event,
				event.addSequenceItem(schema.comments, new Comment(call.user,text))
			);
	}
	@NeedsNoAuthorization
	public void handleRemoveComment(WikiventsCall call, Event event) {
		String commentId=call.request.getParameter("commentId");
		if (event==null || commentId==null)
			return;
		Comment com=event.findComment(commentId);
		if (com.mayBeChangedBy(call.user))
			table.update(event,	event.removeSequenceItem(schema.comments, com));
	}

	public void handleAddOrganizer(WikiventsCall call, Event event) {
		String username=call.request.getParameter("newOrganizer");
		User newOrganizer=model.usernameIndex.get(username);
		if (newOrganizer==null)
			call.sendError(500, "no organizer");
		else if (! event.hasOrganizer(newOrganizer) && event.hasGuest(newOrganizer))
			table.update(event, event.addSequenceItem(schema.organizers, newOrganizer.getRef()));
	}
	public void handleRemoveOrganizer(WikiventsCall call, Event event) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no organizer");
		else if (event.organizers.size()>1) // never remove the last organizer
			table.update(event, event.removeSequenceItem(schema.organizers, user.getRef()));
	}

	public void handleAddGroup(WikiventsCall call, Event event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else if (! event.hasGroup(gr))
			table.update(event, event.addSequenceItem(schema.groups, gr.getRef()));
	}
	public void handleRemoveGroup(WikiventsCall call, Event event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else
			table.update(event, event.removeSequenceItem(schema.groups, gr.getRef()));
	}
	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().eventEdit, data); }
		public Form(WikiventsCall call, CompiledTemplate theme) { super(call, theme); }
		public Form(WikiventsCall call) { this(call, call.getTheme().eventEdit); }

		public final InputField organizer=new InputField("organizer", ((WikiventsCall) call).user._id);
		public final InputField title = new InputField(schema.title);
		public final InputField imageUrl = new InputField(schema.imageUrl);
		public final InputField date= new InputField(schema.date);
		public final InputField time = new InputField(schema.time);
		public final InputField endTime = new InputField(schema.endTime);
		public final InputField max = new InputField(schema.max);
		public final InputField guestsAllowed = new InputField(schema.guestsAllowed);
		public final InputField backupGuestsAllowed= new InputField(schema.backupGuestsAllowed);
		public final InputField city = new InputField(schema.city);
		public final InputField location = new InputField(schema.location);
		public final InputField cost = new InputField(schema.cost);
		public final InputField guestInfo = new InputField(schema.guestInfo);
		public final InputField description= new InputField(schema.description);
		public final InputField idea= new InputField(schema.idea);
	}
}

