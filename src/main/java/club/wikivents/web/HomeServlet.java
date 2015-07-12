package club.wikivents.web;

import java.util.HashMap;

import org.kisst.props4j.Props;

import club.wikivents.model.WikiventsModel;

public class HomeServlet extends TemplateServlet {

	private final WikiventsModel model;

	public HomeServlet(WikiventsModel model, Props props) { 
		super(props);
		this.model=model;
	}

	@Override protected void addContext(HashMap<String, Object> root) {
		root.put("model", model);
		root.put("events", model.events);
		root.put("users", model.users);
	}

	
}
