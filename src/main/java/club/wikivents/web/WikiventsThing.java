package club.wikivents.web;

import org.kisst.http4j.handlebar.TemplateEngine;

import club.wikivents.model.WikiventsModel;

public class WikiventsThing {
	public final WikiventsSite site;
	public final WikiventsModel model;
	public final TemplateEngine engine;

	public WikiventsThing(WikiventsSite site) {
		this.site=site;
		this.model=site.model;
		this.engine=site.engine;
	}

}
