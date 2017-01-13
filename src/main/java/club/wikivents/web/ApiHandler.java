package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.item4j.json.JsonOutputter;

import club.wikivents.model.WikiventsModel;

public class ApiHandler implements HttpCallHandler {
	private final WikiventsModel model;
	private final JsonOutputter json=new JsonOutputter("\t");


	public ApiHandler(WikiventsSite site) {
		this.model=site.model;
	}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		Object obj=null;
		if ("tags".equals( subPath))
			obj=model.tags;
		if (obj!=null)
			call.output(json.createString(obj));
		else
			call.sendError(404, "No such page");
	}
}

