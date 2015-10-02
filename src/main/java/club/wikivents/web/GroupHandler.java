package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;

import club.wikivents.model.Comment;
import club.wikivents.model.Event;
import club.wikivents.model.Group;
import club.wikivents.model.User;

public class GroupHandler extends WikiventsActionHandler<Group> {
	public GroupHandler(WikiventsSite site) { super(site, site.model.groups);	}

	@Override protected Group findRecord(String id) {
		Group result=super.findRecord(id);
		if (result!=null)
			CallInfo.instance.get().data=result.title;
		return result;
	}


	@NeedsNoAuthentication
	public void listAll(WikiventsCall call) { call.output(call.getTheme().groupList, call.model.groups); }

	@NeedsNoAuthentication
	public void view(WikiventsCall call, Group gr) {
		call.output(call.getTheme().groupShow, gr);
	}
	public void viewEdit(WikiventsCall call, Group gr) {
		new Form(call,gr).showForm();
	}

	public void handleEdit(WikiventsCall call, Group oldRecord) {
		Form formdata = new Form(call,null);
		if (formdata.isValid()) 
			model.groups.updateFields(oldRecord, formdata.record);
		formdata.handle();
	}
	public void viewCreate(WikiventsCall call) {
		new Form(call).showForm();
	}
	
	public void handleCreate(WikiventsCall call) {
		Form formdata = new Form(call);
		if (formdata.isValid()) {
			Group t=new Group(call.model,call.user,formdata.record);
			model.groups.create(t);
			call.redirect("/group/:"+t._id);
		}
		else
			formdata.handle();
	}
	public void handleDelete(WikiventsCall call, Group gr) {
		if (call.user.isAdmin)
			table.delete(gr);
	}

	public void handleChangeField(WikiventsCall call, Group oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}

	
	@NeedsNoAuthorization
	public void handleAddComment(WikiventsCall call, Group gr) {
		String text=call.request.getParameter("comment");
		gr.addComment(model, call.user, text);
	}
	@NeedsNoAuthorization
	public void handleAddMember(WikiventsCall call, Group gr) {
		gr.addMember(model, call.user);
	}
	@NeedsNoAuthorization
	public void handleRemoveMember(WikiventsCall call,Group gr) {
		String userId=call.request.getParameter("userId");
		if (call.user._id.equals(userId) || call.user.isAdmin)
			gr.removeMember(model,userId);
	}
	public void handleAddOwner(WikiventsCall call, Group gr) {
		String username=call.request.getParameter("newOwner");
		User newOwner=model.usernameIndex.get(username);
		if (newOwner==null)
			call.sendError(500, "no owner");
		else
			gr.addOwner(model, newOwner);
	}
	public void handleRemoveOwner(WikiventsCall call, Group gr) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no owner");
		else
			gr.removeOwner(model,user);
	}
	
	public void handleRemoveComment(WikiventsCall call, Group gr) {
		String commentId=call.request.getParameter("commentId");
		if (gr==null || commentId==null)
			return;
		Comment com=gr.findComment(commentId);
		model.groups.removeSequenceItem(gr, Group.schema.comments, com);
	}

	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		event.addLike(model, call.user);
	}
	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		event.removeLike(model,call.user);
	}

	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().groupEdit, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().groupEdit); }

		public final InputField owner=new InputField("owner", call.userid);
		public final InputField title = new InputField(Group.schema.title);
		public final InputField description= new InputField(Group.schema.description);
	}
}

