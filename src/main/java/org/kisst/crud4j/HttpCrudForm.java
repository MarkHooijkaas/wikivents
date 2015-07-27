package org.kisst.crud4j;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.http4j.handlebar.TemplateEngine;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public abstract class HttpCrudForm<T extends CrudObject> {
	public class Data extends FormData {
		public final HttpCall call;
		public Data(HttpCall call, Struct record) { super(record);  this.call=call; }
		public boolean isAuthorized() { return HttpCrudForm.this.isAuthorized(record, call); }
	}
	
	protected final CrudTable<T> table;
	private final TemplateEngine engine;
	private final CompiledTemplate template;
	private final String name;
	
	public HttpCrudForm(CrudTable<T> table, TemplateEngine engine, String name) {
		this.table=table;
		this.engine=engine;
		this.name=name;
		this.template=compileTemplate("edit");
	}
	
	public CompiledTemplate compileTemplate(String actionName) { return engine.compileTemplate(name+"/"+actionName);}

	public abstract FormData createFormData(HttpCall call, Struct struct);
	public abstract boolean isAuthorized(Struct record, HttpCall call);

	public void handleCreate(HttpCall call, String subPath) {
		if (call.isGet()) {
			TemplateData context=new TemplateData(call);
			//context.add("form", null);
			template.output(context, call.getWriter());
		}
		else if (call.isPost()) {
			FormData input=createFormData(call,new HttpRequestStruct(call));
			if (input.isValid()) {
				T rec=table.createObject(input.record);
				table.create(rec);
				call.redirect("show/"+rec._id);
			}
			else {
				TemplateData context=new TemplateData(call);
				context.add("form", input);
				template.output(context, call.getWriter());
			}
		}
		else
			call.invalidPage();
	}

	public void handleEdit(HttpCall call, String subPath) {
		T oldRecord = table.read(subPath);
		if (! isAuthorized(oldRecord, call)) {
			// TODO
		}
		if (call.isGet()) {
			TemplateData context=new TemplateData(call);
			context.add("form", createFormData(call,oldRecord));
			template.output(context, call.getWriter());
		}
		else if (call.isPost()) {
			FormData input=createFormData(call,new HttpRequestStruct(call));
			if (input.isValid()) {
				T newRecord=table.createObject(new MultiStruct(input.record,oldRecord));
				System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
				table.update(oldRecord, newRecord);
				call.redirect("../show/"+newRecord._id);
			}
			else {
				TemplateData context=new TemplateData(call);
				context.add("form", input);
				template.output(context, call.getWriter());
			}
		}
		else
			call.invalidPage();
	}
}
