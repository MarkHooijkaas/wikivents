package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.SecureToken;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.StructHelper;

import club.wikivents.model.User;

public class LoginPage extends WikiventsThing {
	public LoginPage(WikiventsSite site) { super(site); }

	
	public class Fields extends HttpFormData {
		public Fields(WikiventsCall call) { super(call,call.getTheme().login); }

		public final User user=findUser(StructHelper.getString(record, "username","").trim());
		public final InputField username = new InputField("username", this::validateUsername);
		public final InputField password = new InputField("password", this::validatePassword);

		// TODO: what is correct behaviour? Should we validate these fields??? w 
		public String validateUsername(InputField f) { 
			if (username!=null && user==null)
				return "Onbekende gebruiker";
			return null;
		} 
		public String validatePassword(InputField f) { 
			if (user==null)
				return null;
			if (user.checkPassword(f.value))
				return null;
			return "Incorrect password";
		} 
		@Override public boolean isValid() { return super.isValid() && user!=null; } 
	};

	private User findUser(String name) {
		if (name.indexOf("@")>0)
			return model.emailIndex.get(name);
		return model.usernameIndex.get(name);
	}


	public void handleLogout(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);
		if (call.isPost() && "true".equals(call.request.getParameter("logout"))) {
			call.clearUserCookie();
			call.redirect("/home");
		}
	}

	
	public void handleLogin(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);

		Fields data = new Fields(call);
		if (call.isGet())
			data.showForm();
		else {
			if (data.isValid()) {
				call.setUserCookie(data.user);
				call.redirect("/user/"+data.user.username);
			}
			else
				data.handle();
		}
	}
	
	public void handleForgotPassword(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);
		String username=call.request.getParameter("username");
		User user=findUser(username);
		if (user!=null) {
			String token = new SecureToken(call.model, user._id).getToken();
			String url= call.getTopUrl()+"/user/"+user.username+"?loginToken="+token;
			TemplateData context=call.createTemplateData();
			context.add("url", url);
			String message = call.getTheme().userForgotPassword.toString(context);
			user.sendMailFrom(User.systemMailAddress, "Inloggen op wikivents", message, false);
			call.redirect("/");
		}
	}

}