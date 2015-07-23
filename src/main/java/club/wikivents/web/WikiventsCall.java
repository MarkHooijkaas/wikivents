package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpUserCall;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class WikiventsCall extends HttpUserCall {
	public final WikiventsModel model;
	public final User user;
	
	protected WikiventsCall(WikiventsCall call) {
		super(call);
		this.model=call.model;
		this.user=call.user;
	}
	public WikiventsCall(HttpCall call, WikiventsModel model) {
		super(call);
		this.model=model;
		this.user=getUser();
	}
	/*
	public WikiventsCall(String path, HttpServletRequest request,HttpServletResponse response, WikiventsModel model) {
		super(path, request, response);
		this.model=model;
		this.user=getUser();
	}
*/
	public TemplateData createTemplateData() { 
		TemplateData data = new TemplateData(model);
		data.add("user", user);
		data.add("model", model);
		//data.add("events", model.events);
		return data;
	}
	
	private User getUser() {
		if (userid==null)
			return null;
		else
			return model.users.read(userid);
	}

	@Override public void ensureUser() {
		if (user==null)
			throw new UnauthorizedException("Not Authenticated");
	}
	@Override public void ensureUser(String userid) {
		ensureUser();
		if (!userid.equals(user._id))
			throw new UnauthorizedException("Not Authorized");
	}
}
