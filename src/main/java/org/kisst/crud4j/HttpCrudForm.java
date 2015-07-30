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

	abstract public T createObject(Struct input);
	public abstract FormData createFormData(HttpCall call, Struct struct);
	public abstract boolean isAuthorized(Struct record, HttpCall call);

	protected boolean authenticationRequiredForCreate() { return true; } // otherwise registration page will not work
	public void handleCreate(HttpCall call, String subPath) {
		if (authenticationRequiredForCreate())
			call.ensureUser();
		if (call.isGet()) {
			TemplateData context=new TemplateData(call);
			context.add("form", createFormData(call,null));
			template.output(context, call.getWriter());
		}
		else if (call.isPost()) {
			FormData input=createFormData(call,new HttpRequestStruct(call));
			if (input.isValid()) {
				T rec=createObject(input);
				table.create(rec);
				if (call.userid==null)
					call.redirect("/home"); // so a newly registered user will get a homepage
				else
					call.redirect("show/"+rec.getString("_id"));
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
		call.ensureUser();
		T oldRecord = createObject(table.read(subPath));
		//if (! isAuthorized(oldRecord, call)) {
			// TODO
		//}
		if (call.isGet()) {
			TemplateData context=new TemplateData(call);
			context.add("form", createFormData(call,oldRecord));
			template.output(context, call.getWriter());
		}
		else if (call.isPost()) {
			FormData input=createFormData(call,new HttpRequestStruct(call));
			if (input.isValid()) {
				T newRecord=createObject(new MultiStruct(input.record,oldRecord));
				System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
				table.update(oldRecord, newRecord);
				call.redirect("../show/"+newRecord.getString("_id"));
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
