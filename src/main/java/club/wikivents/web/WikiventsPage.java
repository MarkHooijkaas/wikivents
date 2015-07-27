package club.wikivents.web;

import org.kisst.http4j.HttpBasicPage;
import org.kisst.http4j.handlebar.TemplateEngine;

import club.wikivents.model.WikiventsModel;

public class WikiventsPage extends HttpBasicPage{
	public final WikiventsModel model;
	public final WikiventsSite site;
	public final TemplateEngine engine;
	public WikiventsPage(WikiventsSite site) { 
		this.site=site;
		this.model=site.model;
		this.engine=site.engine;
	}
}
