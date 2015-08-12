package org.kisst.http4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.kisst.util.ReflectionUtil;
import org.kisst.util.StringUtil;

import club.wikivents.web.WikiventsCall;

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
	private final Class<?>[] signature;
	public ActionHandler(Class<C> callClass, Class<T> recordClass) {
		this.signature= new Class<?>[] { callClass, recordClass };
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
		String id=subPath; 
		String listName=null;
		T record=null;
		if (id!=null && id.trim().length()>0 ) {
			if (id.equals("*"))
				listName="All";
			else if (id.startsWith("*."))
				listName=id.substring(2);
			else if (id.startsWith("*"))
				listName=id.substring(1);
			else
				record=findRecord(id);
		}
		if (record==null && listName==null)
			throw new IllegalArgumentException("Could not find "+id);
		if ("GET".equals(call.request.getMethod().toUpperCase()))
			handleGet(call, record, listName);
		else 
			handlePost(call, record);

	}
	
	private void handleGet(C call, T record, String listName) {
		String methodName="view";
		String view = call.request.getParameter("view");
		if (listName!=null)
			methodName="list"+StringUtil.capitalize(listName);
		else if (view!=null)
			methodName += StringUtil.capitalize(view);
		invoke(methodName, call, record);

	}
	private void handlePost(C call, T record) {
		String methodName="handle";
		String action = call.request.getParameter("action");
		if (record==null)
			methodName="updateAll";
		if (action==null)
			call.throwUnauthorized("No action specified");
		else
			methodName+=StringUtil.capitalize(action);
		invoke(methodName, call, record);	
		//System.out.println(call.response.getStatus());
		if (!call.isAjax()) 
			call.redirect(call.request.getRequestURI());
	}

	private void invoke(String methodName, C call, T record) {
		//System.out.println("id="+id+", method="+methodName+", rec="+record);
		Method method = ReflectionUtil.getMethod(this.getClass(), methodName, signature);
		if (method==null)
			throw new RuntimeException("Unknown method "+methodName);
		NeedsNoAuthentication ann = method.getAnnotation(NeedsNoAuthentication.class);
		if (ann==null && record!=null) { 
			call.ensureUser();
			NeedsNoAuthorization ann2 = method.getAnnotation(NeedsNoAuthorization.class);
			if (ann2==null)
				checkChangeAccess(call, methodName, record);
		}
		ReflectionUtil.invoke(this, method, new Object[]{ call, record});
	}

	
	abstract protected T findRecord(String id);

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthentication { }

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthorization{ }

	abstract protected void checkChangeAccess(C call, String methodName, T oldRecord);
	abstract protected void checkViewAccess(WikiventsCall call, String methodName, T record);
}


