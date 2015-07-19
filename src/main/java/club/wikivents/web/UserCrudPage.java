package club.wikivents.web;

import org.kisst.crud4j.CrudPage;
import org.kisst.http4j.handlebar.GenericForm;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class UserCrudPage extends CrudPage<User> implements WikiventsPage {

	private final  WikiventsModel model;
	public UserCrudPage(WikiventsSite site) {
		super(site, site.model.users,
			new GenericForm(site)
				.addField(User.schema.username, "Gebruikersnaam")
				.addEmailField(User.schema.email, "Email adres")
				.addPasswordField(User.schema.password, "Wachtwoord")
			);
		this.model=site.model;
	}
	@Override public WikiventsModel getModel() { return model;}


}
