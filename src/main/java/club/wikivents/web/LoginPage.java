package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class LoginPage extends TemplatePage {
	public LoginPage(WikiventsSite site) {
		super(site, "login.form");
	}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);
		if (call.isGet()) {
			//System.out.println(user);
			TemplateData data = new TemplateData(call);
			call.output(template.toString(data));
		}
		else if (call.isPost()) {
			if ("true".equals(call.request.getParameter("logout"))) {
				call.clearCookie();
				call.redirect("");
				return;
			}
			Struct data=new HttpRequestStruct(call.request);
			for (String field:data.fieldNames())
				System.out.println(field+"="+data.getObject(field));
			String username=User.schema.username.getString(data);
			String password=User.schema.password.getString(data);
			System.out.println("login from"+username+"  password:"+password);
			String message="Unknown user";
			for (User u: this.model.users) { // TODO: use index on username and on email
				System.out.println("Checking  "+u);
				if (u.username.equals(username) || u.email.equals(username)) {
					System.out.println("Found  "+u);
					if (u.password.equals(password) ) {
						call.setCookie(u._id);
						call.redirect("/user/show/"+u._id);
						return;
					}
					message="password incorrect";
					break;
				}
			}
			TemplateData data2 = new TemplateData(call);
			data2.add("message", message);
			data2.add("username", username);
			data2.add("password", password);
			call.output(template, data2);
		}
	}
}
