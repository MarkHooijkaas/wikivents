package club.wikivents.web;

import java.lang.reflect.Method;

import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.index.UniqueIndex;
import org.kisst.util.CallInfo;
import org.kisst.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsCommands;
import club.wikivents.model.WikiventsCommands.ChangeFieldCommand;
import club.wikivents.model.WikiventsCommands.Command;
import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsObject;

public abstract class WikiventsActionHandler<T extends WikiventsObject<T> & AccessChecker<User> & HasUrl> extends ActionHandler<WikiventsCall, T>{
	public static final Logger logger = LoggerFactory.getLogger(WikiventsActionHandler.class);

	public final WikiventsModel model;
	public final WikiventsSite site;
	public final PkoTable<T> table;
	private final UniqueIndex<T> index;

	private final CompiledTemplate historyTemplate;


	public WikiventsActionHandler(WikiventsSite site, PkoTable<T> table, UniqueIndex<T> index) {
		super(WikiventsCall.class, (Class<T>) table.getElementClass());
		this.site=site;
		this.model=site.model;
		this.table=table;
		this.index=index;
		historyTemplate= model.site.defaultTheme.template("include/show.history");
	}
	
	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		handleCall(call, subPath);
	}

	public void viewHistory(WikiventsCall call, T record) {
		call.output(historyTemplate, record);
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
				T newRecord=cmd.apply();
				if (newRecord!=record)
					table.update(record, newRecord);
			}
			else
				logger.error(cmdName+" not allowed for user "+call.user);
		}
		if (!call.isAjax() && ! call.response.isCommitted()) 
			call.redirect(call.getLocalUrl());
	}

	@Override protected T findRecord(WikiventsCall call, String id) {
		T result;
		if (id.startsWith(":"))
			result= table.read(id.substring(1));
		else {
			result= index.get(id);
			if (result==null) {
				id=call.request.getParameter("id");
				if (id!=null)
					result= table.read(id);
			}
		}
		if (result!=null)
			CallInfo.instance.get().data=result.getName();
		return result;
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
		T newRecord = oldRecord.changeField(fieldName, value);
		table.update(oldRecord, newRecord);
		String oldUrl=oldRecord.getUrl();
		String newUrl=newRecord.getUrl();
		if (!oldUrl.equals(newUrl)) {
			call.response.setHeader("AJAX_REDIRECT", newUrl); //call.redirect(newUrl);
			System.out.println("REDIRECT "+oldUrl+" --> "+newUrl);
		}

	}
	
	public ChangeFieldCommand<T> createChangeFieldCommand(WikiventsCall call, T record) {
		String fieldName=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		return new WikiventsCommands.ChangeFieldCommand<T>(record, fieldName, value);
	}
	
	public void handleDelete(WikiventsCall call, T rec) {
		if (call.user.isAdmin)
			table.delete(rec);
	}
}
