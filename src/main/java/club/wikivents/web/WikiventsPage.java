package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpServer;
import org.kisst.http4j.handlebar.HttpHandlebarPage;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public abstract class WikiventsPage extends HttpHandlebarPage {
	public final WikiventsModel model;

	public WikiventsPage(WikiventsSite site) {
		super(site);
		this.model=site.model;
	}
	
	public Data createTemplateData(HttpServletRequest request) { 
		Data data = new Data();
		data.add("user", getUser(request));
		data.add("model", model);
		//data.add("events", model.events);
		return data;
	}

	
	
	public User getUser(HttpServletRequest request) {
		String id = getUserId(request);
		//System.out.println("searching user: "+id);
		if (id==null)
			return null;
		User user = model.users.read(id);
		//System.out.println("recognized user: "+user);
		return user;
	}
	public User ensureUser(HttpServletRequest req, HttpServletResponse resp) {
		User u=getUser(req);
		if (u==null)
			throw new HttpServer.HttpException(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
		return u;
	}
	public User ensureUser(HttpServletRequest req, HttpServletResponse resp,String usernameOrId) {
		User u=ensureUser(req,resp);
		if (u.username.equals(usernameOrId))
			return u;
		if (u._id.equals(usernameOrId))
			return u;
		throw new HttpServer.UnauthorizedException("Not Authorized expected ["+usernameOrId+"] but user=["+u._id+"]");
	}
}
