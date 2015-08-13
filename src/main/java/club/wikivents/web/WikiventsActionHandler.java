package club.wikivents.web;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable;
import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.AccessChecker;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public abstract class WikiventsActionHandler<T extends CrudObject & AccessChecker<User>> extends ActionHandler<WikiventsCall, T>{
	public final WikiventsModel model;
	public final WikiventsSite site;
	private final CrudTable<T> table;
	public WikiventsActionHandler(WikiventsSite site, CrudTable<T> table) {
		super(WikiventsCall.class, (Class<T>) table.getElementClass());
		this.site=site;
		this.model=site.model;
		this.table=table;
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
			call.throwUnauthorized("User "+call.user.username+" is not authorized to call changing method "+methodName);
	}
	@Override protected void checkViewAccess(WikiventsCall call, String methodName, T record) {
		if (! record.mayBeViewedBy(call.user))
			call.throwUnauthorized("User "+call.user.username+" is not authorized to call viewing method "+methodName);
	}
	

	@Override protected T findRecord(String id) {
		if (id.startsWith(":"))
			id=id.substring(1);
		return table.read(id);
	}

	public void handleChangeField(WikiventsCall call, T oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}

	
}
