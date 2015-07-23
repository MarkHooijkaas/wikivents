package club.wikivents.web;

import org.kisst.http4j.HttpBasicPage;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.WikiventsModel;

public class WikiventsPage extends HttpBasicPage<WikiventsCall>{
	public final WikiventsModel model;
	public final WikiventsSite site;
	public final TemplateEngine engine;
	public WikiventsPage(WikiventsSite site) { 
		this.site=site;
		this.model=site.model;
		this.engine=site.engine;
	}
	public CompiledTemplate createTemplate(String templateName) { return engine.compileTemplate(templateName);}
	public TemplateData createTemplateData() { return new TemplateData(this); }
}
