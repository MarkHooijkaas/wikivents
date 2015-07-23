package club.wikivents.web;

import org.kisst.crud4j.HttpCrudHandler;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.TemplateEngine;

import club.wikivents.model.User;

public class UserCrudPage extends WikiventsPage {
	private final Form form;
	private final HttpCrudHandler<User> handler;

	public class Form extends GenericForm {
		public Form(TemplateEngine engine) { super(engine,"user/user."); addAllFields(); }
		public final TextField username = new TextField(User.schema.username, "Gebruikersnaam");
		public final EmailField email = new EmailField(User.schema.email, "Email adres");
		public final PasswordField password = new PasswordField(User.schema.password, "Wachtwoord");
	}

	
	public UserCrudPage(WikiventsSite site) {
		super(site);
		this.form = new Form(site.engine);
		this.handler=new HttpCrudHandler<User>(model.users, form);
	}
	@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
}
