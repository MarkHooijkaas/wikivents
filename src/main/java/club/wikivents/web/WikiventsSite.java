package club.wikivents.web;



import java.io.File;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

public class WikiventsSite implements HttpCallHandler<HttpCall> {
	public final WikiventsModel model;
	public final TemplateEngine engine;

	private HttpCallHandler<WikiventsCall> handler; 
	
	public WikiventsSite(Struct props) {
		//getStruct("site",Struct.EMPTY)
		this.model=WikiventsModels.createModel(props);
		this.engine=new TemplateEngine(props.getStruct("handlebars"));
		this.handler= 
				new HttpCallDispatcher<WikiventsCall>(new TemplatePage(this, "404"))
				.addHandler("", new TemplatePage(this, "home"))
				.addHandler("users", new TemplatePage(this, "users"))
				.addHandler("events", new TemplatePage(this, "events"))
				.addHandler("user", new UserCrudPage(this))
				.addHandler("event", new EventCrudPage(this))
				.addHandler("login", new LoginPage(this))
				.addHandler("resources", new ResourceHandler<WikiventsCall>(new ResourceHandler.FileResourceFinder(new File("src/resources"))))
				.addHandler("favicon.ico", new ResourceHandler<WikiventsCall>(new ResourceHandler.FileResourceFinder(new File("src/resources/favicon.ico"))))
			;
	}
	@Override public void handle(HttpCall call, String subPath) { handler.handle(new WikiventsCall(call, model), subPath);}
	
	public void close() { model.close(); }

}
