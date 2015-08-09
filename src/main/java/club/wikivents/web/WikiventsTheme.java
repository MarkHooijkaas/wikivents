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
	
	public CompiledTemplate home = template("home");
	public CompiledTemplate login = template("login");
	public CompiledTemplate logout = template("logout");
	public CompiledTemplate sendMessage = template("sendMessage");

	public CompiledTemplate userShow = template("user.show");
	public CompiledTemplate userEdit = template("user.edit");
	public CompiledTemplate userList = template("user.list");

	public CompiledTemplate eventShow = template("event.show");
	public CompiledTemplate eventEdit = template("event.edit");
	public CompiledTemplate eventList = template("event.list");

}
