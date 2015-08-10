package club.wikivents.web;

import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateTheme;
import org.kisst.props4j.Props;

import club.wikivents.model.User;

public class WikiventsTheme extends TemplateTheme {
	public WikiventsTheme(Props props) { super(createEngine(props)); 
	}
	private static TemplateEngine createEngine(Props props) {
		TemplateEngine engine=new TemplateEngine(props);
		engine.registerUserHelpers(User.class, "authenticatedUser");
		return engine;
	}
	
	public final CompiledTemplate home = template("home");
	public final CompiledTemplate login = template("login");
	public final CompiledTemplate logout = template("logout");
	public final CompiledTemplate sendMessage = template("sendMessage");

	public final CompiledTemplate userShow = template("user.show");
	public final CompiledTemplate userEdit = template("user.edit");
	public final CompiledTemplate userList = template("user.list");

	public final CompiledTemplate eventShow = template("event.show");
	public final CompiledTemplate eventEdit = template("event.edit");
	public final CompiledTemplate eventList = template("event.list");

	public final CompiledTemplate pageEdit = template("page.edit");;
	public final CompiledTemplate pageShow = template("page.show");;

}
