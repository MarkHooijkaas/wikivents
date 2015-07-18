package club.wikivents.web;

import org.kisst.crud4j.CrudPage;
import org.kisst.http4j.handlebar.GenericForm;

import club.wikivents.model.User;

public class UserCrudPage extends CrudPage<User> {

	public UserCrudPage(WikiventsSite site) {
		super(site, site.model.users,
			new GenericForm(site)
				.addField(User.schema.username, "Gebruikersnaam")
				.addEmailField(User.schema.email, "Email adres")
				.addPasswordField(User.schema.password, "Wachtwoord")
			);
	}

}
