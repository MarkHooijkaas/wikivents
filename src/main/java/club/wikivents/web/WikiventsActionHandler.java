package club.wikivents.web;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudTable;
import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.util.CallInfo;

import club.wikivents.model.Event;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public abstract class WikiventsActionHandler<T extends CrudObject & AccessChecker<User>> extends ActionHandler<WikiventsCall, T>{
	public final WikiventsModel model;
	public final WikiventsSite site;
	public final CrudTable<T> table;
	public WikiventsActionHandler(WikiventsSite site, CrudTable<T> table) {
		super(WikiventsCall.class, (Class<T>) table.getElementClass());
		this.site=site;
		this.model=site.model;
		this.table=table;
	}
	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		handleCall(call, subPath);
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
		String logValue=value;
		if (logValue.length()>10)
			logValue=logValue.substring(0, 7)+"...";
		CallInfo.instance.get().action="handleChangeField "+field+" to "+logValue;
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}
	
	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		if (! event.isLikedBy(call.user))
			model.events.addSequenceItem(event, Event.schema.likes, new User.Ref(model, call.user._id));
	}
	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		User.Ref ref = new User.Ref(model, call.user._id);
		model.events.removeSequenceItem(event, Event.schema.likes, ref);
	}
	
}
