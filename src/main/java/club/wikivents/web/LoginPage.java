package club.wikivents.web;

import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class LoginPage extends TemplatePage {
	public LoginPage(WikiventsSite site) {
		super(site, "login.form");
	}

	@Override public void handle(WikiventsCall call, String subPath) { new Call(call).handle(subPath); }
	
	private class Call extends WikiventsCall {
		public Call(WikiventsCall call) { super(call);}

		@Override public void handleGet(String subPath) {
			System.out.println(user);
			TemplateData data = createTemplateData();
			output(template.toString(data));
		}
		
		@Override public void handlePost(String subPath) {
			if ("true".equals(request.getParameter("logout"))) {
				clearCookie();
				redirect("");
				return;
			}
			Struct data=new HttpRequestStruct(request);
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
						setCookie(u._id);
						redirect("/user/show/"+u._id);
						return;
					}
					message="password incorrect";
					break;
				}
			}
			TemplateData data2 = createTemplateData();
			data2.add("message", message);
			data2.add("username", username);
			data2.add("password", password);
			output(template.toString(data2));
		}
	}
}
