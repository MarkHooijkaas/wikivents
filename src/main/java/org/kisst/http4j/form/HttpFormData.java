
package org.kisst.http4j.form;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.form.HttpPostHandler.HttpPostResult;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.Struct;

public class HttpFormData extends FormData implements HttpPostResult{
	private final CompiledTemplate template;
	public final HttpCall call;
	public final Struct originalData;
	//public HttpFormData(HttpCall call) { this(call,null); }
	public HttpFormData(HttpCall call, CompiledTemplate template) { 
		this(call, template, null);  
	}
	public HttpFormData(HttpCall call, CompiledTemplate template, Struct originalData) { 
		super(originalData==null? new HttpRequestStruct(call.request) : originalData);  
		this.call=call;
		this.template=template;
		this.originalData=originalData;
	}

	@Override public boolean isSuccess() { return isValid();}
	@Override public Struct errorFields() { return null; } //TODO
	@Override public void reshowForm() { showForm(); }

	public void showForm() {
		if (template==null)
			throw new RuntimeException("No template for reshowing form");
		TemplateData context=new TemplateData(call);
		context.add("form", this);
		template.output(context, call.getWriter());
	}
	public void handle() { HttpPostHandler.handleResult(call, this); }
	public void error(String message) { call.invalidPage();}

}
