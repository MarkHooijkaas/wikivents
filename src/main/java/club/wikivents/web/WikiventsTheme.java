package club.wikivents.web;

import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateTheme;
import org.kisst.http4j.handlebar.UserHelpers;
import org.kisst.props4j.Props;

import com.github.jknack.handlebars.Handlebars;

import club.wikivents.model.User;

public class WikiventsTheme extends TemplateTheme {
	public WikiventsTheme(Props props) { super(createEngine(props)); 
	}
	private static TemplateEngine createEngine(Props props) {
		TemplateEngine engine=new TemplateEngine(props);
		Handlebars handlebar = engine.handlebar;
		WikiventsHelpers h = new WikiventsHelpers();
		handlebar.registerHelper("ifMayChange", h.new IfMayChangeHelper()); 
		handlebar.registerHelper("ifMayView",   h.new IfMayViewHelper());
		handlebar.registerHelper("ifInlineEdit",   h.new IfInlineEditHelper());
		handlebar.registerHelpers(h);

		// TODO: the methods in h2 should also be in h, but are not recognized
		UserHelpers<User> h2 = new UserHelpers<User>(User.class, "authenticatedUser");
		handlebar.registerHelpers(h2);
		

		return engine;
	}
	
	public final CompiledTemplate home = template("home");
	public final CompiledTemplate help = template("help");
	public final CompiledTemplate login = template("login");
	public final CompiledTemplate logout = template("logout");
	public final CompiledTemplate sendMessage = template("sendMessage");
	public final CompiledTemplate error = template("404");

	public final CompiledTemplate userShow = template("user.show");
	public final CompiledTemplate userEdit = template("user.edit");
	public final CompiledTemplate userList = template("user.list");
	public final CompiledTemplate userProfileTemplate = template("user.profile.template");
	public final CompiledTemplate userRegister = template("user.register");
	public final CompiledTemplate userRegisterSucces = template("user.register.success");
	public final CompiledTemplate userSetPassword = template("user.setPassword");

	public final CompiledTemplate eventShow = template("event.show");
	public final CompiledTemplate eventEdit = template("event.edit");
	public final CompiledTemplate eventList = template("event.list");

	public final CompiledTemplate pageEdit = template("page.edit");;
	public final CompiledTemplate pageShow = template("page.show");;

}
