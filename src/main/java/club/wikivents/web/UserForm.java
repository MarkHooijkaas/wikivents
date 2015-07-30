package club.wikivents.web;

import org.kisst.crud4j.HttpCrudForm;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class UserForm extends HttpCrudForm<User> {
	public class Form extends Data {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final Field username = new Field("username");
		public final Field  email   = new Field("email", this::validateEmail);
		public final Field password = new Field("password", this::validateStrongPassword);
	}
	private final WikiventsModel model;		

	public UserForm(WikiventsSite site) {
		super(site.model.users, site.engine, "user");
		this.model=site.model;
	}
	@Override protected boolean authenticationRequiredForCreate() { return false; } // otherwise you need to be logged in to create a user
	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (call.userid==null)
			return false;
		return call.userid.equals(oldRecord.getString("_id" ,null));
	}
	@Override public User createObject(Struct input) { return new User(model,input); }
}
