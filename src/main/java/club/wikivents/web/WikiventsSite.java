package club.wikivents.web;



import java.io.File;

import org.kisst.crud4j.CrudTable;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

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
		public CharSequence ensure(String userid, CrudTable<User>.Ref userref) {  
			if (userid==null)
				return "***";
			if (userref==null)
				return "nobody";
			if (userref._id==null)
				return "no-one";
			User u=userref.get();
			if (u==null)
				return "unknown";
			return u.username;
		}
	}
}
