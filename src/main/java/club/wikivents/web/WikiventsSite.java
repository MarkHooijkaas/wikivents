package club.wikivents.web;



import java.io.File;

import org.kisst.crud4j.CrudRef;
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
		public final HttpCallHandler resources=new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources")));
		public final HttpCallHandler css=new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/css")));
		public final HttpCallHandler scripts=new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/scripts")));
		public final HttpCallHandler js=new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/js")));
		public final HttpCallHandler images=new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/images")));
		@HttpCallDispatcher.Path(dispatchPath="favicon.ico")
		public final HttpCallHandler favicon =new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/favicon.ico")));
	}
	public WikiventsSite(Struct props) {
		this.model=WikiventsModels.createModel(props);
		this.engine=new TemplateEngine(props.getStruct("handlebars"));
		engine.registerHelpers(new Helper());
		this.pages=new Pages();
		this.handler = new HttpCallDispatcher(pages);
	}
	@Override public void handle(HttpCall call, String subPath) { this.handler.handle(call, subPath);}


	public void close() { model.close(); }

	public class Helper {
		private boolean isLoggedIn(Object call) {
			//System.out.println("Checking loggedIn for "+call);
			if (call instanceof User)
				return true;
			if (call instanceof WikiventsCall) {
				if (((WikiventsCall)call).authenticatedUser!=null)
					return true;
			}
			return false;
		}
		public CharSequence priv(Object call, Object obj) {  
			if (isLoggedIn(call))
				return ""+obj;
			return "***";
		}

		public CharSequence privUser(Object call, Object obj) {  
			if (isLoggedIn(call))
				return user(obj);
			return "***";
		}

		@SuppressWarnings("unchecked")
		public CharSequence user(Object obj) {  
			if (obj==null)
				return "nobody";
			if (obj instanceof CrudRef) {
				try {
					return ((CrudRef<User>) obj).get().username;
				}
				catch(RuntimeException e) {return "Unknown("+obj+")"; }
			}
			if (obj instanceof String) {
				try {
					User u=model.users.read((String) obj);
					return u.username;
				}
				catch(RuntimeException e) {return "unknown("+obj+")"; }
			}
			return "invalidref("+obj+")";
		}

	}
}
