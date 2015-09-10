package org.kisst.http4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.kisst.util.ReflectionUtil;
import org.kisst.util.StringUtil;

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
	private final Class<?>[] fullsignature;
	private final Class<?>[] shortsignature;
	public ActionHandler(Class<C> callClass, Class<T> recordClass) {
		this.fullsignature= new Class<?>[] { callClass, recordClass };
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
				record=findRecord(id);
				if (record==null)
					throw new IllegalArgumentException("Could not find "+id);
			}
		}
		invoke(methodName, call, record);
	}
	private void handlePost(C call, String id) {
		String  id2 = call.request.getParameter("ActionHandlerId");
		if (id2!=null)
			id=id2;
		String action = call.request.getParameter("action");
		T record=null;
		if (id!=null && id.trim().length()>0 ) {
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
		if (action==null)
			call.throwUnauthorized("No action specified");
		String methodName="handle"+StringUtil.capitalize(action);
		invoke(methodName, call, record);	
		//System.out.println(call.response.getStatus());
		if (!call.isAjax() && ! call.response.isCommitted()) 
			call.redirect(call.request.getRequestURI());
	}

	private void invoke(String methodName, C call, T record) {
		//System.out.println("id="+id+", method="+methodName+", rec="+record);
		Method method;
		if (record==null) 
			method = ReflectionUtil.getMethod(this.getClass(), methodName, shortsignature);
		else
			method = ReflectionUtil.getMethod(this.getClass(), methodName, fullsignature);

		if (method==null)
			throw new RuntimeException("Unknown method "+methodName);
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
		else
			ReflectionUtil.invoke(this, method, new Object[]{ call, record});
	}

	
	abstract protected T findRecord(String id);

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthentication { }

	@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) 
	public @interface NeedsNoAuthorization{ }

	abstract protected void checkChangeAccess(C call, String methodName, T oldRecord);
	abstract protected void checkViewAccess(C call, String methodName, T record);
}


