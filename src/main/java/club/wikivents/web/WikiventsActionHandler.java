package club.wikivents.web;

import java.lang.reflect.Method;

import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoTable;
import org.kisst.util.CallInfo;
import org.kisst.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.Event;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsCommands;
import club.wikivents.model.WikiventsCommands.ChangeFieldCommand;
import club.wikivents.model.WikiventsCommands.Command;
import club.wikivents.model.WikiventsModel;

public abstract class WikiventsActionHandler<T extends BasicPkoObject<WikiventsModel,T> & AccessChecker<User>> extends ActionHandler<WikiventsCall, T>{
	public static final Logger logger = LoggerFactory.getLogger(WikiventsActionHandler.class);

	public final WikiventsModel model;
	public final WikiventsSite site;
	public final PkoTable<T> table;
	public WikiventsActionHandler(WikiventsSite site, PkoTable<T> table) {
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
	
	@Override protected void handleCommand(String cmdName, WikiventsCall call, T record) {
		CallInfo.instance.get().action=cmdName;
		Method method = ReflectionUtil.getMethod(this.getClass(), "create"+cmdName+"Command", fullsignature);
		if (method==null)
			throw new RuntimeException("Unknown commandName "+cmdName);
		@SuppressWarnings("unchecked")
		Command<T> cmd = (Command<T>) ReflectionUtil.invoke(this, method, new Object[]{ call, record});
		if (cmd==null)
			logger.error("Unknown or invalid command "+cmdName);
		else {
			if (cmd.mayBeDoneBy(call.user)) {
				//System.out.println("Applying "+cmd);
				T newRecord=cmd.apply();
				if (newRecord!=record)
					table.update(record, newRecord);
			}
			else
				System.out.println("Not Allowed "+cmd);
		}
		if (!call.isAjax() && ! call.response.isCommitted()) 
			call.redirect(call.getLocalUrl());
	}

	@Override protected T findRecord(String id) {
		if (id.startsWith(":"))
			id=id.substring(1);
		return table.read(id);
	}
	

	public void handleChangeField(WikiventsCall call, T oldRecord) {
		String fieldName=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		if (fieldName==null || ! oldRecord.fieldMayBeChangedBy(fieldName, call.user))
			return;
		String logValue=value;
		if (logValue.length()>10)
			logValue=logValue.substring(0, 7)+"...";
		CallInfo.instance.get().action="handleChangeField "+fieldName+" to "+logValue;
		table.update(oldRecord, oldRecord.changeField(fieldName, value));
	}
	
	public ChangeFieldCommand<T> createChangeFieldCommand(WikiventsCall call, T record) {
		String fieldName=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		return new WikiventsCommands.ChangeFieldCommand<T>(record, fieldName, value);
	}
	
	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		if (! event.isLikedBy(call.user))
			model.events.update(event, event.addSequenceItem(Event.schema.likes, call.user.getRef()));
	}
	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		User.Ref ref = call.user.getRef();
		model.events.update(event, event.removeSequenceItem(Event.schema.likes, ref));
	}
	
}
