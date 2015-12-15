package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.SecureCookie;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class WikiventsCall extends HttpCall {
	public static final int    LOGIN_DURATION = 7*24*60*60; // a week in seconds
	public static final String LOGIN_COOKIE = "wikilogin";

	public final WikiventsModel model;
	public final User user;
	public final User authenticatedUser;
	public final WikiventsCall call;
	public final boolean authenticated;
	
	public static class BlockedUserException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public final User user;
		public BlockedUserException(User u) { this.user=u; }
	}
	
	private WikiventsCall(HttpCall call, WikiventsModel model) {
		super(call);
		this.model=model;
		SecureCookie cookie = SecureCookie.of(this, LOGIN_COOKIE);
		String userid = cookie==null ? null : cookie.data;
		if (userid==null)
			this.user=null;
		else {
			User u=model.users.readOrNull(userid); 
			if (u!=null && !cookie.isValid(LOGIN_DURATION, u.passwordSalt)) {
				u=null;
				//String url=this.getLocalUrl();
				//if (! (url.endsWith("/login") || url.startsWith("/resources") || url.startsWith("/favicon.ico") || url.startsWith("/lb") || url.startsWith("/css"))) {
				//	//redirect("/login");
				//}
			}
			if (u!=null && u.blocked)
				throw new BlockedUserException(u);
			if (u!=null && u.isAdmin) {
				String uname = request.getParameter("viewAs");
				if (uname!=null)
					u=model.usernameIndex.get(uname); 
			}
			this.user=u;
		}
		this.authenticatedUser=user;
		this.call=this;
		this.authenticated=(user!=null);
		if (user!=null)
			callinfo.user=user.username+" ("+request.getRemoteAddr()+")";
		else
			callinfo.user=request.getRemoteAddr();
	}	
	public static WikiventsCall of(HttpCall httpcall, WikiventsModel model) {
		if (httpcall instanceof WikiventsCall)
			return (WikiventsCall) httpcall;
		return new WikiventsCall(httpcall,model);
	}
	
	public void setRememberedUserName(String value) { setNamedCookieValue("rememberUserName", value, 365*24*3600); }
	public String getRememberedUserName() { return getNamedCookieValue("rememberUserName", null); }

	public void expireLogin() {
	}

	@Override public boolean isAuthenticated() { return user!=null; }
	@Override public void ensureUser() { if (user==null) throwUnauthorized("Not Authenticated"); }
	public void ensureSameUser(User u) { 
		if (user==null) 
			throwUnauthorized("Not Authenticated user");
		if (! user._id.equals(u._id))
			throwUnauthorized("Not Authorized user");
	}

	@Override public void sendError(int code, String message) {
		response.setStatus(code);
		TemplateData context = createTemplateData();
		context.add("message", message);
		output(getTheme().error,context);
		//try { response.sendError(code, message); } catch (IOException e) { throw new RuntimeException(e);}
	}

	
	public TemplateData createTemplateData() { return new TemplateData(this); }
	public void output(CompiledTemplate template) { template.output(new TemplateData(this), getWriter());}
	public void output(CompiledTemplate template, TemplateData data) { template.output(data, getWriter());}
	public void output(CompiledTemplate template, Iterable<?> list) { template.output(new TemplateData(this).add("list", list), getWriter());}
	//public void output(CompiledTemplate template, TypedSequence<?> list) { template.output(new TemplateData(this).add("list", list), getWriter());}
	public void output(CompiledTemplate template, Struct record) { template.output(new TemplateData(this).add("record", record), getWriter());}


	public WikiventsTheme getTheme() {
		String theme=request.getParameter("wikiventsTheme");
		if (theme==null)
			theme="default";
		return model.site.getTheme(theme);
	}
	
	public void clearUserCookie() { SecureCookie.clear(this, LOGIN_COOKIE); }
	public void setUserCookie(User user) {
		SecureCookie.set(this, LOGIN_COOKIE, user._id, LOGIN_DURATION, user.passwordSalt);
	}
}
