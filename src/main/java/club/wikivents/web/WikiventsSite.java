package club.wikivents.web;



import java.io.File;

import org.kisst.item4j.struct.Struct;
import org.kisst.servlet4j.HttpHandlebarSite;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

public class WikiventsSite extends HttpHandlebarSite {
	public final WikiventsModel model;
	private MongoClient mongoClient=null;
	
	public WikiventsSite(Struct props) {
		super(props);
		String storage = props.getString("storage", "file");
		if ("file".equals(storage))
			this.model=WikiventsModels.createFileModel(new File(props.getString("datadir", "data")));
		else if ("mongo".equals(storage)) {
			mongoClient = new MongoClient(props.getString("mongohost", "localhost"));
			DB db = new DB(mongoClient,props.getString("mongodb", "wikivents"));
			this.model=WikiventsModels.createMongoModel(db);
		}
		else
			throw new RuntimeException("Unknown storage type "+storage);
		addPages();
	}
	
	private void addPages() { 
		addPage("default", new TemplatePage(this, "home"));
		//addPage("/config", new ConfigServlet(props));
		addPage("/login", new LoginPage(this));
	}
}
