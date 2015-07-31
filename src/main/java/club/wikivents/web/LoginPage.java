package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.http4j.handlebar.HttpForm;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class LoginPage extends WikiventsPage {
	private final HttpForm<Fields> form=new HttpForm<Fields>(engine.compileTemplate("login.form"), this::fields);
	public LoginPage(WikiventsSite site) { super(site); }

	public class Fields extends FormData {
		public final User user=model.usernameIndex.get(record.getString("username",null));
		public final Field username = new Field("username", this::validateUsername);
		public final Field password = new Field("password", this::validatePassword);

		public Fields(Struct record) { super(record); }
		
		// TODO: what is correct behaviour? Should we validate these fields??? w 
		public String validateUsername(Field f) { return (user!=null) ? null : "Unknown username "+f.value;} 
		public String validatePassword(Field f) { 
			if (user==null)
				return null;
			if (user.password==null)
				return null;
			if (user.password.equals(f.value) )
				return null;
			return "Incorrect password";
		} 

	};
	
	public Fields fields(HttpCall call, String subPath, Struct input) { return new Fields(input); }
	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);
		if (call.isPost() && "true".equals(call.request.getParameter("logout"))) {
			call.clearCookie();
			call.redirect("");
			return;
		}
		Fields result = form.handle(call, subPath);
		if (result==null)
			return;
		if (result.user.password==null) { // user has no password, so don't let him/her log in 
			call.redirect("");
			return; 
		}
		if (! result.user.password.equals(result.password.value) ) 
			throw new RuntimeException("Invalid login");
		call.setCookie(result.user._id);
		call.redirect("/user/show/"+result.user._id);
	}
}