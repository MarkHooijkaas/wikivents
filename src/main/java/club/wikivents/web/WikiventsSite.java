package club.wikivents.web;

import java.io.File;
import java.util.HashMap;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCall.UnauthorizedException;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallDispatcher.Path;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.HttpServer.PageRedirectedException;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.props4j.Props;
import org.kisst.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.WikiventsModels;
import club.wikivents.model.WikiventsModel;
import club.wikivents.web.WikiventsCall.BlockedUserException;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

public class WikiventsSite implements HttpCallHandler {
	final static Logger logger=LoggerFactory.getLogger(WikiventsSite.class);
	
	public final WikiventsModel model;
	//public final TemplateEngine engine;
	public final HttpCallHandler handler;
	public final Pages pages;
	public final LoginPage loginPage;
	public final WikiventsTheme defaultTheme;  
	public final Props props;
	public final MailSender emailer;
	public final String recaptchaPrivateKey;
	public final String recaptchaPublicKey;

	private final HashMap<String, WikiventsTheme> themes= new HashMap<String, WikiventsTheme>();
	
	public class Pages {
		public final HttpCallHandler home=new TemplatePage(WikiventsSite.this, "home");
		public final HttpCallHandler help=new TemplatePage(WikiventsSite.this, "help");
		public final HttpCallHandler user=new UserHandler(WikiventsSite.this);
		public final HttpCallHandler event=new EventHandler(WikiventsSite.this);
		public final HttpCallHandler theme=new GroupHandler(WikiventsSite.this);
		public final HttpCallHandler login  = loginPage::handleLogin;
		public final HttpCallHandler logout = new LogoutPage(WikiventsSite.this);
		public final HttpCallHandler search = new SearchPage(WikiventsSite.this);
		public final HttpCallHandler sendMessage = new SendMessagePage(WikiventsSite.this);
		private final File resourceDir=new File(props.getString("resourceDir","resources"));
		//public final HttpCallHandler lib=new ResourceHandler("lib/", new File(resourceDir, "lib"));
		//public final HttpCallHandler css=new ResourceHandler("css/", new File(resourceDir,"css"));
		//public final HttpCallHandler images=new ResourceHandler("images/", new File(resourceDir,"images"));
		//@Path(dispatchPath="favicon.ico")
		//public final HttpCallHandler favicon =new ResourceHandler("resources/favicon.ico", new File(resourceDir,"images/favicon.ico"));
		@Path(dispatchPath="*")
		public final HttpCallHandler defaultHandler =new ResourceHandler(null, resourceDir);
	}
	public WikiventsSite(Props props) {
		this.props=props;
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
		this.model=WikiventsModels.createModel(this, props);
		this.loginPage=new LoginPage(this);
		this.pages=new Pages();
		this.handler = new HttpCallDispatcher(pages);
		this.recaptchaPublicKey=props.getString("recaptchaPublicKey");
		this.recaptchaPrivateKey=props.getString("recaptchaPrivateKey");

	}
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = null;
		try {
			call = WikiventsCall.of(httpcall, model);
			this.handler.handle(call, subPath);
		}
        catch (PageRedirectedException e) { logger.info("redirect to {} from {}",e.url, httpcall.request.getRequestURI()); }
		catch (RuntimeException e) {
			if (e instanceof UnauthorizedException)
				logger.warn("User {} unauthorized to call {}", call==null? null : call.user, httpcall.getLocalUrl());
			else
				logger.error("Error when handling "+httpcall.request.getRequestURL(), e);
			TemplateData context = new TemplateData(httpcall);
			context.add("exception", e);
			CompiledTemplate templ = this.defaultTheme.error;
			if (e instanceof BlockedUserException)
				templ=this.defaultTheme.blockedUser;
			httpcall.output(templ.toString(context));
		}
	}

	public void close() { model.close(); }
	public WikiventsTheme getTheme(String themeName) { return themes.get(themeName); }

	public String captchaHtml() {
		ReCaptcha c = ReCaptchaFactory.newSecureReCaptcha(recaptchaPublicKey, recaptchaPrivateKey, false);
		String result = c.createRecaptchaHtml(null, null);
		result=result.replace("https://api-secure.recaptcha.net/", "https://www.google.com/recaptcha/api/");
		return result;
	}

}
