package org.kisst.http4j.form;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.Struct;

public class HttpForm<T extends FormData>{
	private final CompiledTemplate template;
	private final FormDataCreator<T> creator;
	
	public HttpForm(CompiledTemplate template, FormDataCreator<T> creator) {
		this.template=template;
		this.creator=creator;
	}

	@FunctionalInterface
	public interface FormDataCreator<T> {
		public T createFormData(HttpCall call, String subPath, Struct input);
	}

	public T handle(HttpCall call, String subPath) {
		if (! (call.isGet() || call.isPost())) {
			call.invalidPage();
			return null;
		}
		T data = creator.createFormData(call,subPath, new HttpRequestStruct(call));
		if (call.isPost() && data.isValid())
			return data;
		TemplateData context=new TemplateData(call);
		context.add("form", data);
		template.output(context, call.getWriter());
		return null;
	}
}
