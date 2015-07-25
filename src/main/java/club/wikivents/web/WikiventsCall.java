package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpUserCall;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
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
		if (userid==null)
			this.user=null;
		else
			this.user=model.users.read(userid);
	}
	public TemplateData createTemplateData() { return new TemplateData(this); }
	
	public void output(CompiledTemplate template, TemplateData data) { template.output(data, getWriter());}

	@Override public void ensureUser() {
		if (user==null)
			throw new UnauthorizedException("Not Authenticated");
	}
}
