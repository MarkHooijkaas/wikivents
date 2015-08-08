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
	
	CompiledTemplate home = template("home");
	CompiledTemplate login = template("login");
	CompiledTemplate logout = template("logout");
	CompiledTemplate userShow = template("user.show");
	CompiledTemplate userEdit = template("user.edit");
	CompiledTemplate userList = template("user.list");

	CompiledTemplate eventShow = template("event.show");
	CompiledTemplate eventEdit = template("event.edit");
	CompiledTemplate eventList = template("event.list");

}
