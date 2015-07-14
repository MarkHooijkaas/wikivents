package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplatePage extends WikiventsPage {
	private final Template template;

	public TemplatePage(WikiventsSite site, String templateName) { 
		super(site);
		this.template = createTemplate(templateName);
	}

	@Override public void handle(HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		//Sequence<User> users=model.users.findAll();
		//data.add("users", users);
		//data.add("model", model);
		//data.add("user", model.users.get(0));
		template.output(data, response);
	}

	
}
