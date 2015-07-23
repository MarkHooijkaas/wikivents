package club.wikivents.web;

public class UserCrudPage extends WikiventsPage {
	public UserCrudPage(WikiventsSite site) {
		super(site);
		
			//new GenericForm(site)
			//	.addField(User.schema.username, "Gebruikersnaam")
			//	.addEmailField(User.schema.email, "Email adres")
			//	.addPasswordField(User.schema.password, "Wachtwoord")
			//);
	}

	@Override public void handle(WikiventsCall call, String subPath) {
		// TODO Auto-generated method stub
		
	}
}
