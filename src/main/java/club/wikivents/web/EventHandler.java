package club.wikivents.web;

import java.io.File;

import club.wikivents.model.Wikivent;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.Group;
import club.wikivents.model.Tag;

public class EventHandler extends CommonBaseHandler<Wikivent> {
	public static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

	public final Wikivent.Schema schema;
	private final ResourceHandler uploads = new ResourceHandler(null, new File("data/Wikivent/"));


	public EventHandler(WikiventsSite site) { super(site, site.model.events, site.model.eventUrlIndex, Wikivent.schema); this.schema= Wikivent.schema; }

	@NeedsNoAuthentication
	public void listAll(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.events); }
	@NeedsNoAuthentication
	public void listFuture(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.futureEvents()); }
	@NeedsNoAuthentication
	public void listPast(WikiventsCall call) { call.output(call.getTheme().eventList, call.model.pastEvents()); }

	public void listExport(WikiventsCall call) { call.output(call.getTheme().eventExport, call.model.futureEvents()); }
	public void listExportNew(WikiventsCall call) { call.output(call.getTheme().eventExportNew, call.model.futureEvents()); }

	@NeedsNoAuthentication
	public void view(WikiventsCall call, Wikivent event) {
		call.output(call.getTheme().eventShow, event);
	}
	public void viewEdit(WikiventsCall call, Wikivent event) {
		new Form(call,event).showForm();
	}

	public void handleEdit(WikiventsCall call, Wikivent event) {
		Form formdata = new Form(call,(Struct) null);
		if (formdata.isValid()) 
			table.update(event, event.changeFields(formdata.record));
		formdata.handle();
	}
	public void viewCreate(WikiventsCall call) {
		Wikivent toClone=null;
		String clone=call.request.getParameter("clone");
		if (clone!=null) 
			toClone=model.events.readOrNull(clone);
		if (toClone==null)
			new Form(call).showForm();
		else
			new Form(call,toClone).showForm();
	}
	public void viewCreateIdea(WikiventsCall call) {
		new Form(call, call.getTheme().eventCreateIdea).showForm();
	}
	
	public void handleCreateIdea(WikiventsCall call) { handleCreate(call); }
	public void handleCreate(WikiventsCall call) {
			if (! call.user.mayOrganize())
			throw new RuntimeException("User "+call.user.username+" not allowed to create event "+call.request.getParameter("title") );
		Form formdata = new Form(call);
		if (formdata.isValid()) {
			Wikivent e=new Wikivent(call.model,call.user,formdata.record);
			CallInfo.instance.get().data=e.title;
			table.create(e);
			call.redirect("/event/:"+e._id);
		}
		else
			formdata.handle();
	}

	@NeedsNoAuthentication
	public void viewUploads(WikiventsCall call, Wikivent event, String subpath) {
		uploads.handle(call, event._id+".dir/uploads/"+subpath);
	}


	public void handleAddGroup(WikiventsCall call, Wikivent event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else if (! event.hasGroup(gr))
			table.update(event, event.addSequenceItem(schema.groups, gr.getRef()));
	}
	public void handleRemoveGroup(WikiventsCall call, Wikivent event) {
		String id=call.request.getParameter("groupId");
		Group gr=model.groups.read(id);
		if (gr==null)
			call.sendError(500, "no such group");
		else
			table.update(event, event.removeSequenceItem(schema.groups, gr.getRef()));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void handleAddTag(WikiventsCall call, Wikivent rec) {
		String tag=call.request.getParameter("tag");
		tag= Tag.normalize(tag);
		CallInfo.instance.get().action="handleAddTag "+tag;
		if (!rec.tags.endsWith(","))
			tag=","+tag;
		if (! rec.hasTag(tag))
			table.update(rec, rec.changeField(schema.tags, rec.tags+tag+","));
	}
	public void handleRemoveTag(WikiventsCall call, Wikivent rec) {
		String tag=call.request.getParameter("tag");
		tag= Tag.normalize(tag);
		CallInfo.instance.get().action="handleRemoveTag "+tag;
		if (rec.hasTag(tag))
			table.update(rec, rec.changeField(schema.tags, rec.tags.replaceAll(tag+",","")));
	}

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().eventEdit, data); }
		public Form(WikiventsCall call, CompiledTemplate theme) { super(call, theme); }
		public Form(WikiventsCall call) { this(call, call.getTheme().eventEdit); }

		public final InputField owner=new InputField("owner", ((WikiventsCall) call).user._id);
		public final InputField title = new InputField(schema.title);
		public final InputField imageUrl = new InputField(schema.imageUrl);
		public final InputField date= new InputField(schema.date);
		public final InputField time = new InputField(schema.time);
		public final InputField endTime = new InputField(schema.endTime);
		public final InputField max = new InputField(schema.max);
		public final InputField membersAllowed = new InputField(schema.membersAllowed);
		public final InputField backupMembersAllowed= new InputField(schema.backupMembersAllowed);
		public final InputField city = new InputField(schema.city);
		public final InputField location = new InputField(schema.location);
		public final InputField cost = new InputField(schema.cost);
		public final InputField guestInfo = new InputField(schema.guestInfo);
		public final InputField description= new InputField(schema.description);
		public final InputField idea= new InputField(schema.idea);
		public final InputField invitedOnly = new CheckBoxField(schema.invitedOnly);
		public final InputField hidden= new CheckBoxField(schema.hidden);
	}
}

