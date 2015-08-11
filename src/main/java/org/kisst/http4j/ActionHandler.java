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
		String id=subPath; 
		T record=null;
		if (id!=null && id.trim().length()>0) {
			record=findRecord(id);
			if (record==null)
				throw new IllegalArgumentException("Could not find record "+id);
		}

		String methodName="view";
		if ("GET".equals(call.request.getMethod().toUpperCase())) {
			String view = call.request.getParameter("view");
			if (record==null)
				methodName="listAll";
			else
				methodName="view";
			if (view!=null)
				methodName += StringUtil.capitalize(view);
		}
		else {
			String action = call.request.getParameter("action");
			if (record==null)
				methodName="updateAll";
			else
				methodName="handle";
			if (action!=null)
				call.throwUnauthorized("No action specified");
			else
				methodName+=StringUtil.capitalize(action);
		}
		//System.out.println("id="+id+", method="+methodName+", rec="+record);
		Method method = ReflectionUtil.getMethod(this.getClass(), methodName, signature);
		if (method==null)
			throw new RuntimeException("Unknown method "+methodName);
		NeedsNoAuthentication ann = method.getAnnotation(NeedsNoAuthentication.class);
		if (ann==null) { 
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


