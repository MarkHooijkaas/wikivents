package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginPage extends WikiventsPage {
	private final Template loginForm;
	
	public LoginPage(WikiventsSite site) {
		super(site);
		loginForm=createTemplate("login.form");
	}

	@Override public void handleGet(HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		loginForm.output(data, response);
	}
	
	

}
