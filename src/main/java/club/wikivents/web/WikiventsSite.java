package club.wikivents.web;

import java.util.HashMap;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.ResourceHandler;
import org.kisst.item4j.struct.Struct;

import club.wikivents.WikiventsModels;
import club.wikivents.model.WikiventsModel;

public class WikiventsSite implements HttpCallHandler {
	public final WikiventsModel model;
	//public final TemplateEngine engine;
	public final HttpCallHandler handler;
	public final Pages pages;
	public final LoginPage loginPage;
	public final  WikiventsTheme defaultTheme;  

	private final HashMap<String, WikiventsTheme> themes= new HashMap<String, WikiventsTheme>();
	
	public class Pages {
		public final HttpCallHandler home=new TemplatePage(WikiventsSite.this, "home");
		public final HttpCallHandler user=new UserPage(WikiventsSite.this);
		public final HttpCallHandler event=new EventPage(WikiventsSite.this);
		public final HttpCallHandler login  = loginPage::handleLogin;
		public final HttpCallHandler logout = new LogoutPage(WikiventsSite.this);
		public final HttpCallHandler resources=new ResourceHandler("resources/", "src/resources");
		public final HttpCallHandler css=new ResourceHandler("resources/css/", "src/resources/css");
		public final HttpCallHandler scripts=new ResourceHandler("resources/scripts/", "src/resources/scripts");
		public final HttpCallHandler js=new ResourceHandler("resources/js/", "src/resources/js");
		public final HttpCallHandler images=new ResourceHandler("resources/images/", "src/resources/images");
		@HttpCallDispatcher.Path(dispatchPath="favicon.ico")
		public final HttpCallHandler favicon =new ResourceHandler("resources/favicon.ico", "src/resources/favicon.ico");
	}
	public WikiventsSite(Struct props) {
		this.model=WikiventsModels.createModel(this, props);
		//this.engine=new TemplateEngine(props.getStruct("handlebars"));
		//engine.registerUserHelpers(User.class, "authenticatedUser");
		Struct themeProps = props.getStruct("theme");
		for (String name: themeProps.fieldNames())
			themes.put(name, new WikiventsTheme(themeProps.getStruct(name)));
		this.defaultTheme=themes.get("default");
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
	public WikiventsTheme getTheme(String themeName) { return themes.get(themeName); }

}
