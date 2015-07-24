package club.wikivents.web;

import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

public class TemplatePage extends WikiventsPage {
	public final CompiledTemplate template;

	public TemplatePage(WikiventsSite site, String templateName) {
		super(site);
		this.template = engine.compileTemplate(templateName);
	}

	@Override public void handle(WikiventsCall call, String subPath) {
		TemplateData data = call.createTemplateData();
		call.output(template.toString(data));
	}

	
}
