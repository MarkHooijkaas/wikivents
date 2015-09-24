package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.StructHelper;

import club.wikivents.model.User;

public class LoginPage extends WikiventsThing {
	public LoginPage(WikiventsSite site) { super(site); }

	public class Fields extends HttpFormData {
		public Fields(WikiventsCall call) { super(call,call.getTheme().login); }

		public final User user=model.usernameIndex.get(StructHelper.getString(record, "username","").trim());
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


	public void handleLogout(HttpCall call, String subPath) {
		if (call.isPost() && "true".equals(call.request.getParameter("logout"))) {
			call.clearCookie();
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
				call.setCookie(data.user._id);
				call.redirect("/user/"+data.user.username);
			}
			else
				data.handle();
		}
	}
}