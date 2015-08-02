
package org.kisst.http4j.form;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpCallHandler;
import org.kisst.item4j.struct.Struct;

/*
 * This class will perform some logic, which results in FormData, which can be valid (success) or not
 * In the request extra information can be used to indicate how the result is to be returned:
 * - if AJAX call
 * 		* if valid:    return OK
 * 		* if invalid:  return json/xml to be used to show the form again
 * - if page call
 * 		* if valid:    redirect to success page
 * 		* if invalid:  show html form again with validation message
 * - a redirect after the form is succesfully processed
 */
public class HttpPostHandler implements HttpCallHandler {
	public HttpPostHandler(Logic logic) {this.logic=logic; }

	public interface HttpPostResult {
		boolean isSuccess();
		Struct errorFields();
		default String successUrl() { return null; }
		void reshowForm();
	}


	@FunctionalInterface
	public interface Logic {
		public HttpPostResult execute(HttpCall call, String subPath);
	}

	private final Logic logic;

	public void handle(HttpCall call, String subPath) {
		if (! call.isPost())
			call.invalidPage();
		else {
			HttpPostResult result = logic.execute(call, subPath);
			handleResult(call, result);
		}
	}

	public static void handleResult(HttpCall call, HttpPostResult result) {
		if (isAjaxCall(result))
			handleAjaxResult(call, result);
		else  // HTML
			handleHtmlResult(call, result);
	}

	public static void handleHtmlResult(HttpCall call, HttpPostResult result) {
		if (result.isSuccess()) {
			String url = call.request.getParameter("successUrl");
			if (url==null)
				url=result.successUrl();
			if (url==null)
				url="/";
			call.redirect(url);
		}
		else 
			result.reshowForm();
	}

	public static void handleAjaxResult(HttpCall call, HttpPostResult result) {
		if (result.isSuccess())
			call.output("OK");
		else {
			Struct data=result.errorFields();
			call.output(data.toString()); // TODO make json, and looked at HTTP headers
		}
	}

	public static boolean isAjaxCall(HttpPostResult result) {	return false; }

}
