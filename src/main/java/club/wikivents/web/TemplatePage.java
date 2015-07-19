package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.handlebar.HttpHandlebarPage;
import org.kisst.http4j.handlebar.HttpHandlebarSite.TemplateData;

import club.wikivents.model.WikiventsModel;

public class TemplatePage extends HttpHandlebarPage implements WikiventsPage {
	private final String url;
	private final Template template;
	public  final  WikiventsModel model;

	public TemplatePage(WikiventsSite site, String url) { 
		super(site);
		this.model=site.model;
		this.url=url;
		String templateName=url;
		if (templateName.endsWith("/*"))
			templateName= templateName.substring(0, templateName.length()-2);
		this.template = createTemplate("wikivents/"+templateName);
	}
	@Override public WikiventsModel getModel() { return model; }
	@Override public String getPath() { return url; }
	@Override public void handle(String path, HttpServletRequest request, HttpServletResponse response) {
		TemplateData data = createTemplateData(request);
		template.output(data, response);
	}
}
