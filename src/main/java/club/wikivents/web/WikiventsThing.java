package club.wikivents.web;

import club.wikivents.model.WikiventsModel;

public class WikiventsThing {
	public final WikiventsSite site;
	public final WikiventsModel model;

	public WikiventsThing(WikiventsSite site) {
		this.site=site;
		this.model=site.model;
	}

}
