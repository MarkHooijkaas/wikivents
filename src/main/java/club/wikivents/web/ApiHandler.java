package club.wikivents.web;

import java.io.PrintWriter;

import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.json.JsonOutputter;

import club.wikivents.model.Tag;
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
	}
}

