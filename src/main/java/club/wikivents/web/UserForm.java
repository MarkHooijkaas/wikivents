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
		public final StringField username = new StringField(schema.username);
		public final StringField  email = new StringField(schema.email, this::validateEmail);
	}		

	public UserForm(WikiventsSite site) {
		super(site.model.users, site.engine, "user");
	}

	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (call.userid==null)
			return false;
		return call.userid.equals(oldRecord.getString("_id" ,null));
	}}
