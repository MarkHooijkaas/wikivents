package club.wikivents.web;



import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.Struct;

import club.wikivents.WikiventsModels;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class WikiventsSite implements HttpCallHandler {
	public final WikiventsModel model;
	public final TemplateEngine engine;
	public final HttpCallHandler handler;
	public final Pages pages;

	public class Pages {
		public final HttpCallHandler home=new TemplatePage(WikiventsSite.this, "home");
		public final HttpCallHandler user=new UserPage(WikiventsSite.this);
		public final HttpCallHandler event=new EventPage(WikiventsSite.this);
		public final HttpCallHandler login=new LoginPage(WikiventsSite.this);
		public final HttpCallHandler resources=new ResourceHandler("", "src/resources");
		public final HttpCallHandler css=new ResourceHandler("", "src/resources/css");
		public final HttpCallHandler scripts=new ResourceHandler("", "src/resources/scripts");
		public final HttpCallHandler js=new ResourceHandler("", "src/resources/js");
		public final HttpCallHandler images=new ResourceHandler("", "src/resources/images");
		@HttpCallDispatcher.Path(dispatchPath="favicon.ico")
		public final HttpCallHandler favicon =new ResourceHandler("", "src/resources/favicon.ico");
	}
	public WikiventsSite(Struct props) {
		this.model=WikiventsModels.createModel(props);
		this.engine=new TemplateEngine(props.getStruct("handlebars"));
		engine.registerUserHelpers(User.class, "authenticatedUser");
		this.pages=new Pages();
		this.handler = new HttpCallDispatcher(pages);
	}
	@Override public void handle(HttpCall call, String subPath) { this.handler.handle(call, subPath);}


	public void close() { model.close(); }

}
