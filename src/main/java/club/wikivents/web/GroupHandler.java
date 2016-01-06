package club.wikivents.web;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;

import club.wikivents.model.Comment;
import club.wikivents.model.Group;
import club.wikivents.model.User;

public class GroupHandler extends WikiventsActionHandler<Group> {
	public final Group.Schema schema;

	public GroupHandler(WikiventsSite site) { super(site, site.model.groups); this.schema=Group.schema;	}

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
		if (! call.user.trusted())
			throw new RuntimeException("User "+call.user.username+" not trusted to create thema "+call.request.getParameter("title") );
		Form formdata = new Form(call);
		if (formdata.isValid()) {
			Group t=new Group(call.model,call.user,formdata.record);
			model.groups.create(t);
			call.redirect("/theme/:"+t._id);
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
		table.addSequenceItem(gr, schema.comments, new Comment(call.user,text));
	}
	@NeedsNoAuthorization
	public void handleAddMember(WikiventsCall call, Group gr) {
		if (!gr.hasMember(call.user))
			table.addSequenceItem(gr, schema.members, call.user.getRef());
	}
	@NeedsNoAuthorization
	public void handleRemoveMember(WikiventsCall call,Group gr) {
		String userId=call.request.getParameter("userId");
		if (call.user._id.equals(userId) || call.user.isAdmin)
			table.removeSequenceItem(gr, schema.members, (User.Ref) model.users.createRef(userId));
	}
	
	public void handleAddOwner(WikiventsCall call, Group gr) {
		String username=call.request.getParameter("newOwner");
		User newOwner=model.usernameIndex.get(username);
		if (newOwner==null)
			call.sendError(500, "no owner");
		else if (! gr.hasOwner(newOwner))
			table.addSequenceItem(gr, schema.owners, newOwner.getRef());
	}
	public void handleRemoveOwner(WikiventsCall call, Group gr) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no owner");
		else if (gr.owners.size()>1) // never remove the last organizer
			table.removeSequenceItem(gr, schema.owners, user.getRef());
	}
	
	public void handleRemoveComment(WikiventsCall call, Group gr) {
		String commentId=call.request.getParameter("commentId");
		if (gr==null || commentId==null)
			return;
		Comment com=gr.findComment(commentId);
		model.groups.removeSequenceItem(gr, Group.schema.comments, com);
	}

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().groupEdit, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().groupEdit); }

		public final InputField owner=new InputField("owner", ((WikiventsCall) call).user._id);
		public final InputField title = new InputField(Group.schema.title);
		public final InputField description= new InputField(Group.schema.description);
	}
}

