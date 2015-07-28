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
	
	private WikiventsCall(HttpCall call, WikiventsModel model) {
		super(call);
		this.model=model;
		if (userid==null)
			this.user=null;
		else
			this.user=model.users.read(userid);
		this.authenticatedUser=user;
	}
	public static WikiventsCall of(HttpCall httpcall, WikiventsModel model) {
		if (httpcall instanceof WikiventsCall)
			return (WikiventsCall) httpcall;
		return new WikiventsCall(httpcall,model);
	}

	
	public TemplateData createTemplateData() { return new TemplateData(this); }
	public void output(CompiledTemplate template, TemplateData data) { template.output(data, getWriter());}
/*	public void output(CompiledTemplate template, Object ... objs) {
		TemplateData context = new TemplateData(this);
		String str="";
		for (Object obj : objs) {
			context.add(obj.getClass().getSimpleName().toLowerCase(), obj);
			str +=obj.getClass().getSimpleName().toLowerCase()+",'";
		}
		context.add("context", str);
		template.output(context, getWriter());
	}
*/

	@Override public void ensureUser() {
		if (user==null)
			throw new UnauthorizedException("Not Authenticated");
	}
}
