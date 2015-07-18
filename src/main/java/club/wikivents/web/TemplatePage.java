package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplatePage extends WikiventsPage {
	private final String url;
	private final Template template;

	public TemplatePage(WikiventsSite site, String url) { 
		super(site);
		this.url=url;
		String templateName=url;
		if (templateName.endsWith("/*"))
			templateName= templateName.substring(0, templateName.length()-2);
		this.template = createTemplate("wikivents/"+templateName);
	}
	@Override public String getPath() { return url; }
	@Override public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		template.output(data, response);
	}

	
}
