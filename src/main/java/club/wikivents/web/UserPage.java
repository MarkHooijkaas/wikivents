package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

public class UserPage extends WikiventsPage {
	public UserPage(WikiventsSite site) { super(site);	}
	public final UserCrudPage crud=new UserCrudPage(site);

	public final HttpCallHandler list=new TemplatePage(site,"user/list", this::listAllUsers);
	public final HttpCallHandler show=new TemplatePage(site,"user/show", this::userRecord);
	public final HttpCallHandler edit=crud::handleEdit;
	public final HttpCallHandler create=crud::handleCreate;
	
	public final HttpCallDispatcher handler=new HttpCallDispatcher(this); 	
	@Override public void handle(HttpCall call, String subPath) { handler.handle(call, subPath); }
	
	public void listAllUsers(TemplateData data, HttpCall httpcall, String subPath) { data.add("list", model.users.findAll()); }
	public void userRecord(TemplateData data, HttpCall httpcall, String subPath) { data.add("record", model.users.read(subPath)); }

}

