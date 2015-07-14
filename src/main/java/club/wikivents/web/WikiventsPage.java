package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.servlet4j.HttpHandlebarPage;

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
		return data;
	}

	
	
	public User getUser(HttpServletRequest request) {
		String id = getUserId(request);
		if (id==null)
			return null;
		return model.users.read(id);
	}
	public User ensureUser(HttpServletRequest req, HttpServletResponse resp,String usernameOrId) {
		User u=getUser(req);
		if (u==null) {
			sendUnauthorizedError(resp);
			throw new RuntimeException("Not Authenticated");
		}
		if (u.username.equals(usernameOrId))
			return u;
		if (u._id.equals(usernameOrId))
			return u;
		sendUnauthorizedError(resp);
		throw new RuntimeException("Not Authorized");
	}
}
