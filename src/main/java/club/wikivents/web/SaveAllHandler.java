package club.wikivents.web;

import org.kisst.http4j.HttpCall;

public class SaveAllHandler extends WikiventsPage {
	public SaveAllHandler(WikiventsSite site) {
		super(site);
	}


	public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		if (! call.user.isAdmin)
			call.throwUnauthorized("naughty");

		model.users.saveAll();
		model.events.saveAll();
		model.groups.saveAll();

		call.output("done");
	}
}
