package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class WikiventsCall extends HttpCall {
	public final WikiventsModel model;
	public final User user;
	public final User authenticatedUser;
	public final WikiventsCall call;
	public final boolean authenticated;
	
	private WikiventsCall(HttpCall call, WikiventsModel model) {
		super(call);
		this.model=model;
		if (userid==null)
			this.user=null;
		else {
			User u=model.users.readOrNull(userid); // TODO cache info in cookie, so table does not to be read with every call
			if (u.isAdmin) {
				String uname = request.getParameter("viewAs");
				if (uname!=null)
					u=model.usernameIndex.get(uname); 
			}
			this.user=u;
		}
		this.authenticatedUser=user;
		this.call=this;
		this.authenticated=(user!=null);
	}
	public static WikiventsCall of(HttpCall httpcall, WikiventsModel model) {
		if (httpcall instanceof WikiventsCall)
			return (WikiventsCall) httpcall;
		return new WikiventsCall(httpcall,model);
	}

	
	public TemplateData createTemplateData() { return new TemplateData(this); }
	public void output(CompiledTemplate template) { template.output(new TemplateData(this), getWriter());}
	public void output(CompiledTemplate template, TemplateData data) { template.output(data, getWriter());}

	@Override public void ensureUser() { if (user==null) throwUnauthorized("Not Authenticated"); }

	public WikiventsTheme getTheme() {
		String theme=request.getParameter("wikiventsTheme");
		if (theme==null)
			theme="default";
		return model.site.getTheme(theme);
	}
}
