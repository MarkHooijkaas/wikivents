package club.wikivents.web;

import static club.wikivents.model.User.schema;

import org.kisst.crud4j.HttpCrudDispatcher;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class UserCrudPage extends HttpCrudDispatcher<User> {
	public class Form extends HttpForm {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final StringField username = new StringField(schema.username);
		public final EmailField  email = new EmailField(schema.email);
	}		

	public UserCrudPage(WikiventsSite site) {
		super(site.model.users, site.engine, "user");
	}

	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (call.userid==null)
			return false;
		return call.userid.equals(oldRecord.getString("_id" ,null));
	}}
