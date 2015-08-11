package club.wikivents.web;

import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.AccessChecker;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public abstract class WikiventsActionHandler<T extends AccessChecker<User>> extends ActionHandler<WikiventsCall, T>{
	public final WikiventsModel model;
	public final WikiventsSite site;
	public WikiventsActionHandler(WikiventsSite site, Class<T> recordClass) {
		super(WikiventsCall.class, recordClass);
		this.site=site;
		this.model=site.model;
	}
	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		if (call.authenticated || subPath.equals("create"))
			handleCall(call, subPath);
		else
			call.throwUnauthorized("You must be logged in to access the user pages");
	}


	@Override protected void checkChangeAccess(WikiventsCall call, String methodName, T record) {
		if (! record.mayBeChangedBy(call.user))
			call.throwUnauthorized("User "+call.user.username+" is not authorized to change this item");
	}
	@Override protected void checkViewAccess(WikiventsCall call, String methodName, T record) {
		if (! record.mayBeViewedBy(call.user))
			call.throwUnauthorized("User "+call.user.username+" is not authorized to view this item");
	}

}
