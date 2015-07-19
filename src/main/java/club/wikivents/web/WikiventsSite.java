package club.wikivents.web;



import java.io.File;

import org.kisst.http4j.HttpPageMap;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.handlebar.HttpHandlebarSite;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

public class WikiventsSite extends HttpHandlebarSite {
	public final WikiventsModel model;
	public final HttpPageMap pages;
	
	public WikiventsSite(Struct props) {
		super(props.getStruct("handlebars",Struct.EMPTY));
		this.model=WikiventsModels.createModel(props);

		pages=new HttpPageMap("",new TemplatePage(this, "404"))
		.addHandler("", new TemplatePage(this, "home"))
		.addPage(new TemplatePage(this, "users"))
		.addPage(new TemplatePage(this, "events"))
		.addPage(new UserCrudPage(this))
		.addPage(new EventCrudPage(this))
		.addPage(new LoginPage(this))
		.addHandler("resources/*", new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources"))))
		.addHandler("/favicon.ico", new ResourceHandler(new ResourceHandler.FileResourceFinder(new File("src/resources/favicon.ico"))));
	}

	public void close() { model.close(); }
}
