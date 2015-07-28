package club.wikivents.web;

import static club.wikivents.model.User.schema;

import org.kisst.crud4j.HttpCrudForm;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class UserForm extends HttpCrudForm<User> {
	public class Form extends Data {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final Field username = new Field(schema.username);
		public final Field  email   = new Field(schema.email, this::validateEmail);
		public final Field password = new Field(schema.password, this::validateStrongPassword);
	}		

	public UserForm(WikiventsSite site) {
		super(site.model.users, site.engine, "user");
	}
	@Override protected boolean authenticationRequiredForCreate() { return false; } // otherwise you need to be logged in to create a user
	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (call.userid==null)
			return false;
		return call.userid.equals(oldRecord.getString("_id" ,null));
	}}
