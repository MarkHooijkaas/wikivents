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
	public final LoginPage loginPage;

	public class Pages {
		public final HttpCallHandler home=new TemplatePage(WikiventsSite.this, "home");
		public final HttpCallHandler user=new UserPage(WikiventsSite.this);
		public final HttpCallHandler event=new EventPage(WikiventsSite.this);
		public final HttpCallHandler login  = loginPage::handleLogin;
		public final HttpCallHandler logout = loginPage::handleLogout;
		public final HttpCallHandler resources=new ResourceHandler("resources/", "src/resources");
		public final HttpCallHandler css=new ResourceHandler("resources/css/", "src/resources/css");
		public final HttpCallHandler scripts=new ResourceHandler("resources/scripts/", "src/resources/scripts");
		public final HttpCallHandler js=new ResourceHandler("resources/js/", "src/resources/js");
		public final HttpCallHandler images=new ResourceHandler("resources/images/", "src/resources/images");
		@HttpCallDispatcher.Path(dispatchPath="favicon.ico")
		public final HttpCallHandler favicon =new ResourceHandler("resources/favicon.ico", "src/resources/favicon.ico");
	}
	public WikiventsSite(Struct props) {
		this.model=WikiventsModels.createModel(props);
		this.engine=new TemplateEngine(props.getStruct("handlebars"));
		engine.registerUserHelpers(User.class, "authenticatedUser");
		this.loginPage=new LoginPage(this);
		this.pages=new Pages();
		this.handler = new HttpCallDispatcher(pages);
		//for (User u :model.users) {
		//	if (u.encryptedPassword==null)
		//		u.setPassword(u.password);
		//}
	}
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		this.handler.handle(call, subPath);
	}


	public void close() { model.close(); }

}
