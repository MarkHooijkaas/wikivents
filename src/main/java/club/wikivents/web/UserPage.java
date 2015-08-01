package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallDispatcher.PostOnly;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.User;

public class UserPage extends WikiventsPage {
	public UserPage(WikiventsSite site) { super(site);	}
	public final UserForm crud=new UserForm(site);

	public final HttpCallHandler list=new TemplatePage(site,"user/list", this::listAllUsers);
	public final HttpCallHandler show=new TemplatePage(site,"user/show", this::userRecord);
	public final HttpCallHandler edit=crud::handleEdit;
	public final HttpCallHandler create=crud::handleCreate;	
	@PostOnly public final HttpCallHandler addFriend=this::handleAddFriend;
	@PostOnly public final HttpCallHandler changePassword=this::changePassword;

	
	public final HttpCallDispatcher handler=new HttpCallDispatcher(this); 	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (call.authenticated || subPath.equals("create"))
			handler.handle(call, subPath);
		else
			call.throwUnauthorized("You must be logged in to access the user pages");
	}
	
	public void listAllUsers(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.users.findAll()); }
	public void userRecord(TemplateData data, HttpCall httpcall, String subPath) { data.add("record", model.users.read(subPath)); }

	public void handleAddFriend(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		call.ensureUser();
		User friend=model.users.read(subPath);
		call.user.addFriend(model, friend);
		call.redirect("../show/"+call.user._id);
	}

	public void changePassword(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		call.ensureUser();
		String newPassword= call.request.getParameter("newPassword");
		String checkNewPassword= call.request.getParameter("checkNewPassword");
		if (! newPassword.equals(checkNewPassword))
			return; // TODO: show message
		call.user.changePassword(newPassword);
		call.redirect("show/"+call.user._id);
	}
}

