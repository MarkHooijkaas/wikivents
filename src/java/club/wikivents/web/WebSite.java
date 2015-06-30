package club.wikivents.web;

import org.kisst.props4j.Props;
import org.kisst.servlet4j.ServletContainer;

import club.wikivents.model.WikiventsModel;

public class WebSite extends ServletContainer {
	private final WikiventsModel model;

	public WebSite(WikiventsModel model,Props props) {
		super(props);
		this.model=model;
	}

	
	@Override public void startListening() { 
		addServlet("default", new HomeServlet(model, props));
		addServlet("/config", new ConfigServlet(props));
        
        super.startListening();
	}


}
