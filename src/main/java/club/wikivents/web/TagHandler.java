package club.wikivents.web;

import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.WikiventsModel;

public class TagHandler extends ActionHandler<WikiventsCall, String> {

	private final WikiventsModel model;

	public TagHandler(WikiventsSite site) { super(WikiventsCall.class, String.class);
		this.model=site.model;
	}

	@NeedsNoAuthentication
	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().tagList, model.tags);
	}

	@NeedsNoAuthentication
	public void view(WikiventsCall call, String tag) {
		TemplateData data=new TemplateData(call);
		data.add("tag", model.tags.getTag(tag));
		data.add("users",model.userTags.records(tag));
		data.add("events",model.eventTags.records(tag));
		call.output(call.getTheme().tagShow, data);
	}


	@Override protected String findRecord(WikiventsCall call, String id) { return id; }

	@Override protected void checkChangeAccess(WikiventsCall call, String methodName, String oldRecord) {}

	@Override protected void checkViewAccess(WikiventsCall call, String methodName, String record) {}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		handleCall(call, subPath);
	}
}

