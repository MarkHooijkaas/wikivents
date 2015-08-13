package club.wikivents.web;

import java.util.ArrayList;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Friend;
import club.wikivents.model.User;

public class UserHandler extends WikiventsActionHandler<User> {
	public UserHandler(WikiventsSite site) { super(site, site.model.users);	}

	@Override protected User findRecord(String id) {
		if (id.startsWith(":"))
			return model.users.read(id.substring(1));
		return model.usernameIndex.get(id);
	}

	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().userList, model.users);
	}
	public void listFriend(WikiventsCall call) {
		ArrayList<User> list=new ArrayList<>();
		for (Friend f: call.user.friends)
			list.add(f.user.get());
		call.output(call.getTheme().userList, list); 
	}

	//@NeedsNoAuthorization
	public void view(WikiventsCall call, User user) {
		call.output(call.getTheme().userShow, user);
	}
	public void viewEdit(WikiventsCall call, User u) {
		new Form(call,u).showForm();
	}
	public void handleEdit(WikiventsCall call, User oldRecord) {
		Form formdata = new Form(call,null);
		if (formdata.isValid()) 
			model.users.updateFields(oldRecord, formdata.record);
		formdata.handle();
	}
	
	public void viewRegister(WikiventsCall call) {
		new Form(call).showForm();
	}
	
	public void handleRegister(WikiventsCall call) {
		Form formdata = new Form(call);
		if (formdata.isValid()) 
			model.users.create(new User(call.model,formdata.record));
		formdata.handle();
	}
	

	public void handleChangeField(WikiventsCall call, User oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}

	public void handleAddAsFriend(WikiventsCall call, User friend) {
		call.user.addFriend(model, friend);
	}

	public void handleChangePassword(WikiventsCall call, User u) {
		String newPassword= call.request.getParameter("newPassword");
		String checkNewPassword= call.request.getParameter("checkNewPassword");
		System.out.println(newPassword+", "+checkNewPassword);
		if (! newPassword.equals(checkNewPassword))
			call.sendError(500, "supplied passwords do not match");
		else
			u.changePassword(newPassword);
	}
	
	public static class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().userEdit, data); }
		public Form(WikiventsCall call) 			 { super(call, call.getTheme().userEdit); }
		public final InputField username = new InputField(User.schema.username);
		public final InputField email    = new InputField(User.schema.email, this::validateEmail);
		public final InputField city     = new InputField(User.schema.city);
		public final InputField avatarUrl= new InputField(User.schema.avatarUrl);
	}
}

