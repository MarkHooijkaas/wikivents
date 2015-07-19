package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpServer;
import org.kisst.http4j.handlebar.HttpHandlebarSite.TemplateData;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public interface WikiventsPage {
	public WikiventsModel getModel();
	public String getUserId(HttpServletRequest request);

	
	default public TemplateData createTemplateData(HttpServletRequest request) { 
		TemplateData data = new TemplateData(null);
		data.add("user", getUser(request));
		data.add("model", getModel());
		//data.add("events", model.events);
		return data;
	}
	
	default public User getUser(HttpServletRequest request) {
		String id = getUserId(request);
		//System.out.println("searching user: "+id);
		if (id==null)
			return null;
		User user = getModel().users.read(id);
		//System.out.println("recognized user: "+user);
		return user;
	}


	default public User ensureUser(HttpServletRequest req, HttpServletResponse resp) {
		User u=getUser(req);
		if (u==null)
			throw new HttpServer.UnauthorizedException("Not Authorized");
		return u;
	}
	default public User ensureUser(HttpServletRequest req, HttpServletResponse resp,String usernameOrId) {
		User u=ensureUser(req,resp);
		if (u.username.equals(usernameOrId))
			return u;
		if (u._id.equals(usernameOrId))
			return u;
		throw new HttpServer.UnauthorizedException("Not Authorized expected ["+usernameOrId+"] but user=["+u._id+"]");
	}
}
