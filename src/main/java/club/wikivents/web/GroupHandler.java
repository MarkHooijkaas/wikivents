package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Comment;
import club.wikivents.model.CommonBase;
import club.wikivents.model.Group;
import club.wikivents.model.User;

public class GroupHandler extends CommonBaseHandler<Group> {
	public final Group.Schema schema;

	public GroupHandler(WikiventsSite site) { super(site, site.model.groups, site.model.groupUrlIndex, Group.schema); this.schema=Group.schema;	}

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
			model.groups.update(oldRecord, oldRecord.changeFields(formdata.record));
		formdata.handle();
	}
	public void viewCreate(WikiventsCall call) {
		new Form(call).showForm();
	}
	
	public void handleCreate(WikiventsCall call) {
		if (! call.user.mayOrganize())
			throw new RuntimeException("User "+call.user.username+" not allowed to create thema "+call.request.getParameter("title") );
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

	@NeedsNoAuthorization
	public void handleAddComment(WikiventsCall call, Group gr) {
		String text=call.request.getParameter("comment");
		table.update(gr, gr.addSequenceItem(schema.comments, new Comment(call.user,text)));
	}
	@NeedsNoAuthorization
	public void handleAddMember(WikiventsCall call, Group gr) {
		if (!gr.hasMember(call.user))
			table.update(gr, gr.addSequenceItem(schema.members, call.user.getRef()));
	}
	@NeedsNoAuthorization
	public void handleRemoveMember(WikiventsCall call,Group gr) {
		String userId=call.request.getParameter("userId");
		if (call.user._id.equals(userId) || call.user.isAdmin)
			table.update(gr, gr.removeSequenceItem(schema.members, User.Ref.of(model, userId)));
	}
	
	public void handleAddOwner(WikiventsCall call, Group gr) {
		String username=call.request.getParameter("newOwner");
		User newOwner=model.usernameIndex.get(username);
		if (newOwner==null)
			call.sendError(500, "no owner");
		else if (! gr.hasOwner(newOwner))
			table.update(gr, gr.addSequenceItem(schema.owners, newOwner.getRef()));
	}
	public void handleRemoveOwner(WikiventsCall call, Group gr) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no owner");
		else if (gr.owners.size()>1) // never remove the last owner
			table.update(gr, gr.removeSequenceItem(schema.owners, user.getRef()));
	}
	
	public void handleRemoveComment(WikiventsCall call, Group gr) {
		String commentId=call.request.getParameter("commentId");
		if (gr==null || commentId==null)
			return;
		Comment com=gr.findComment(commentId);
		table.update(gr, gr.removeSequenceItem(Group.schema.comments, com));
	}

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().groupEdit, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().groupEdit); }

		public final InputField owner=new InputField("owner", ((WikiventsCall) call).user._id);
		public final InputField title = new InputField(Group.schema.title);
		public final InputField description= new InputField(Group.schema.description);
		public final InputField invitedOnly = new CheckBoxField(schema.invitedOnly);
		public final InputField hidden= new CheckBoxField(schema.hidden);
	}
}

