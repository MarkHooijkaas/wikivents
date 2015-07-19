package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.HttpHandlebarPage;
import org.kisst.http4j.handlebar.HttpHandlebarSite.TemplateData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class LoginPage extends HttpHandlebarPage implements WikiventsPage {
	private final Template loginForm;
	private final WikiventsModel model;
	
	public LoginPage(WikiventsSite site) {
		super(site);
		loginForm=createTemplate("login.form");
		this.model=site.model;
	}

	@Override public WikiventsModel getModel() { return this.model; }

	@Override public void handleGet(String path, HttpServletRequest request, HttpServletResponse response) {
		System.out.println(getUser(request));
		TemplateData data = createTemplateData(request);
		loginForm.output(data, response);
	}
	@Override public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {
		if ("true".equals(request.getParameter("logout"))) {
			clearCookie(response);
			redirect(response,"");
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
			if (u.username.equals(username) || u.email.equals(username)) {
				//System.out.println("Found  "+u);
				if (u.password.equals(password) ) {
					setCookie(response,u._id);
					redirect(response,"/");
					return;
				}
				message="password incorrect";
				break;
			}
		}
		TemplateData data2 = createTemplateData(request);
		data2.add("message", message);
		data2.add("username", username);
		data2.add("password", password);
		loginForm.output(data2, response);
		
	}
}
