package club.wikivents.web;

import org.kisst.http4j.HttpCall;

public class LogoutPage extends WikiventsPage {
	public LogoutPage(WikiventsSite site) { super(site); }


	public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (call.isGet()) {
			call.output(call.getTheme().logout);
		}
		else if (call.isPost() && "true".equals(call.request.getParameter("logout"))) {
			call.clearCookie();
			call.redirect("/home");
		}
	}

}