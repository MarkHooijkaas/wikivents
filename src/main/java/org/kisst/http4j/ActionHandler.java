package org.kisst.http4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.kisst.pko4j.BasicPkoObject;
import org.kisst.util.CallInfo;
import org.kisst.util.ReflectionUtil;
import org.kisst.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * will call the following methods:
 * GET
 * 		/<path>/ 				listAll
 * 		/<path>/?view=json      listAllJson
 * 		/<path>/<id>            view
 * 		/<path>/<id>?view=json  viewJson
 *
 * POST
 * 		/<path>/ 				       <not legal, one should always specify an action>
 * 		/<path>/[action=json]          updateAllJson
 * 		/<path>/<id>                   <not legal, one should always specify an action>
 * 		/<path>/<id>[action=addGuest]  handleAddGuest
 */
public abstract class ActionHandler<C extends HttpCall, T> implements HttpCallHandler {
	public static final Logger logger = LoggerFactory.getLogger(ActionHandler.class);
	public static final Logger httpPostLogger = LoggerFactory.getLogger("http.POST");

	private final Class<?>[] extralongsignature;
	protected final Class<?>[] fullsignature;
	private final Class<?>[] fullsignature2;
	private final Class<?>[] shortsignature;
	public ActionHandler(Class<C> callClass, Class<T> recordClass) {
		this.extralongsignature= new Class<?>[] { callClass, recordClass, String.class };
		this.fullsignature= new Class<?>[] { callClass, recordClass };
		this.fullsignature2= new Class<?>[] { callClass, BasicPkoObject.class};
		this.shortsignature= new Class<?>[] { callClass };
	}

	public void handleCall(C call, String subPath) {
		if (call.isAjax()) {
			try {
				handleCall2(call, subPath);
			}
			catch (RuntimeException e) { call.sendError(500, e.getMessage()); }
		}
		else
			handleCall2(call, subPath);
	}


	public void handleCall2(C call, String subPath) {
		if (call.isGet())
			handleGet(call, subPath);
		else 
			handlePost(call, subPath);

	}
	
	private void handleGet(C call, String id) {
		String subpath=null;
		int pos=id.indexOf('/');
		if (pos>0) {
			subpath=id.substring(pos+1);
			id=id.substring(0, pos);
		}
		String methodName=null;
		T record=null;
		if (id!=null && id.trim().length()>0 ) {
			if (id.equals("*"))
				methodName="listAll";
			else if (id.startsWith("*."))
				methodName="list"+StringUtil.capitalize(id.substring(2));
			else if (id.startsWith("*"))
				methodName="list"+StringUtil.capitalize(id.substring(1));
			else if (id.startsWith("!"))
				methodName="view"+StringUtil.capitalize(id.substring(1));
			else {
				String view = call.request.getParameter("view");
				if (view==null)
					methodName = "view";
				else
					methodName = "view"+StringUtil.capitalize(view);
				if (subpath!=null) {
					String view2="";
					int pos2=subpath.indexOf('/');
					if (pos2>0) {
						view2=subpath.substring(0, pos2);
						subpath=subpath.substring(pos2+1);
					}
					methodName += StringUtil.capitalize(view2);
				}
				record=findRecord(id);
				if (record==null)
					throw new IllegalArgumentException("Could not find "+id);
			}
		}
		invoke(methodName, call, record, subpath);
	}
	private void handlePost(C call, String id) {
		if (httpPostLogger.isInfoEnabled())
			httpPostLogger.info(call.toString());
		String  id2 = call.request.getParameter("ActionHandlerId");
		if (id2!=null)
			id=id2;
		String cmdName = call.request.getParameter("command");
		String action = call.request.getParameter("action");
		T record=null;
		if (id!=null && id.trim().length()>0 && ! id.equals("NONE")) {
			if (id.startsWith("!")) {
//				String oldAction = action;
				action=id.substring(1);
//				if (oldAction!=null && oldAction.trim().length()>0 && !oldAction.equals(action)) 
//					throw new IllegalArgumentException("Conflicting actions "+oldAction+" and "+action);
			}
			else {
				record=findRecord(id);
				if (record==null )
					throw new IllegalArgumentException("Could not find "+id);
			}
		}
		if (cmdName!=null) {
			//if (httpPostLogger.isInfoEnabled())
			//	httpPostLogger.info("invoking command "+cmdName+", rec="+record);
			handleCommand(cmdName, call, record);
			return;
		}
		if (action==null) {
			call.throwUnauthorized("No action specified");
		}
		String methodName="handle"+StringUtil.capitalize(action);
		//if (httpPostLogger.isInfoEnabled()) 
		//	httpPostLogger.info("invoking method "+methodName+", rec="+record);
		invoke(methodName, call, record, null);	
		//System.out.println(call.response.getStatus());
		if (!call.isAjax() && ! call.response.isCommitted()) 
			call.redirect(call.getLocalUrl());
	}

	protected void handleCommand(String cmdName, C call, T record) {
		// TODO: make generic, but now is overridden in subclass 
	}

	private void invoke(String methodName, C call, T record, String subpath) {
		CallInfo.instance.get().action=methodName;
		Method method=null;
		boolean extralong=false;
		if (record!=null) {
			method = ReflectionUtil.getMethod(this.getClass(), methodName, extralongsignature);
			if (method==null)
				method = ReflectionUtil.getMethod(this.getClass(), methodName, fullsignature);
			else
				extralong=true;
			if (method==null)
				method = ReflectionUtil.getMethod(this.getClass(), methodName, fullsignature2);
		}
		if (method==null) {
			method = ReflectionUtil.getMethod(this.getClass(), methodName, shortsignature);
			record=null;
		}
		if (method==null) {
			logger.error("Unknown method "+methodName);
			return;
		}
		NeedsNoAuthentication ann = method.getAnnotation(NeedsNoAuthentication.class);
		if (ann==null) { 
			call.ensureUser();
			NeedsNoAuthorization ann2 = method.getAnnotation(NeedsNoAuthorization.class);
			if (ann2==null && record!=null) {
				if (call.isGet())
					checkViewAccess(call, methodName, record);
				else
					checkChangeAccess(call, methodName, record);
			}
		}
		if (record==null)
			ReflectionUtil.invoke(this, method, new Object[]{ call});
		else {
			if (extralong)
				ReflectionUtil.invoke(this, method, new Object[]{ call, record, subpath});
			else
				ReflectionUtil.invoke(this, method, new Object[]{ call, record});
		}
	}

	
	abstract protected T findRecord(String id);

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthentication { }

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthorization{ }

	abstract protected void checkChangeAccess(C call, String methodName, T oldRecord);
	abstract protected void checkViewAccess(C call, String methodName, T record);
}


