package club.wikivents.web;

import java.util.HashMap;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallDispatcher.Path;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.ResourceHandler;
import org.kisst.props4j.Props;
import org.kisst.util.MailSender;

import club.wikivents.WikiventsModels;
import club.wikivents.model.WikiventsModel;

public class WikiventsSite implements HttpCallHandler {
	public final WikiventsModel model;
	//public final TemplateEngine engine;
	public final HttpCallHandler handler;
	public final Pages pages;
	public final LoginPage loginPage;
	public final  WikiventsTheme defaultTheme;  
	public MailSender emailer;

	private final HashMap<String, WikiventsTheme> themes= new HashMap<String, WikiventsTheme>();
	
	public class Pages {
		public final HttpCallHandler home=new TemplatePage(WikiventsSite.this, "home");
		public final HttpCallHandler user=new UserHandler(WikiventsSite.this);
		public final HttpCallHandler event=new EventHandler(WikiventsSite.this);
		public final HttpCallHandler page =new PageHandler(WikiventsSite.this);
		public final HttpCallHandler login  = loginPage::handleLogin;
		public final HttpCallHandler logout = new LogoutPage(WikiventsSite.this);
		public final HttpCallHandler sendMessage = new SendMessagePage(WikiventsSite.this);
		public final HttpCallHandler resources=new ResourceHandler("resources/", "src/resources");
		public final HttpCallHandler css=new ResourceHandler("resources/css/", "src/resources/css");
		public final HttpCallHandler scripts=new ResourceHandler("resources/scripts/", "src/resources/scripts");
		public final HttpCallHandler js=new ResourceHandler("resources/js/", "src/resources/js");
		public final HttpCallHandler images=new ResourceHandler("resources/images/", "src/resources/images");
		@Path(dispatchPath="favicon.ico")
		public final HttpCallHandler favicon =new ResourceHandler("resources/favicon.ico", "src/resources/favicon.ico");
	}
	public WikiventsSite(Props props) {
		this.model=WikiventsModels.createModel(this, props);
		//for (Event e:model.newestEvents)
		//	System.out.println(e._id+"\t"+e.creationDate());
		
		this.emailer=new MailSender(props.getProps("email"));
		Props themeProps = props.getProps("theme",null);
		if (themeProps==null) {
			themes.put("default", new WikiventsTheme(Props.EMPTY_PROPS));
		}
		else {
			for (String name: themeProps.fieldNames())
				themes.put(name, new WikiventsTheme(themeProps.getProps(name)));
		}
		this.defaultTheme=themes.get("default");
		this.loginPage=new LoginPage(this);
		this.pages=new Pages();
		this.handler = new HttpCallDispatcher(pages);
	}
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		this.handler.handle(call, subPath);
	}

	public void close() { model.close(); }
	public WikiventsTheme getTheme(String themeName) { return themes.get(themeName); }

}
