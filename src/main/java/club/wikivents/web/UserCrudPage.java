package club.wikivents.web;

import org.kisst.crud4j.HttpCrudDispatcher;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class UserCrudPage extends WikiventsPage {
	private final Form form;
	private final HttpCrudDispatcher<User> handler;

	public class Form extends GenericForm {
		public Form(TemplateEngine engine) { 
			super(engine,"user/user.",
					User.schema.username,
					User.schema.email,
					User.schema.password);
		}
		@Override public void validateCreate(String userid, HashStruct data) {}
		@Override public void validateUpdate(String userid, Struct oldRecord, Struct newRecord) {}	}

	
	public UserCrudPage(WikiventsSite site) {
		super(site);
		this.form = new Form(site.engine);
		this.handler=new HttpCrudDispatcher<User>(model.users, form);
	}
	@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
}
