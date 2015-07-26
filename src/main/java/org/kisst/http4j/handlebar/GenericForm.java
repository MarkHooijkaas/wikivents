package org.kisst.http4j.handlebar;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpView;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.Schema;
import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public abstract class GenericForm implements HttpView, Struct {
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<String,Field>();
	private final CompiledTemplate showTemplate;
	private final CompiledTemplate editTemplate;
	private final CompiledTemplate listTemplate;

	public GenericForm(TemplateEngine engine, String prefix, Schema<?>.Field ... sfields) {
		this.showTemplate=engine.compileTemplate(prefix+"show");
		this.listTemplate=engine.compileTemplate(prefix+"list");
		this.editTemplate=engine.compileTemplate(prefix+"edit");
		for (Schema<?>.Field f:sfields)
			fields.put(f.getName(), new Field(f));
	}
	public Iterator<Field> fields() { return fields.values().iterator(); }
	@Override public Iterable<String> fieldNames() { return fields.keySet(); }
	@Override public Object getDirectFieldValue(String name) { return fields.get(name); }

	@Override public void showViewPage(HttpCall call, Struct data) { new Instance(call,data).output(showTemplate); }
	@Override public void showEditPage(HttpCall call, Struct data) { new Instance(call,data).output(editTemplate); }
	
	@Override public void showList(HttpCall call, ItemSequence seq) {
		TemplateData context = new TemplateData(call);
		context.add("list", seq);
		call.output(listTemplate.toString(context)); 
	}
	
	public class Instance implements Struct {
		private final LinkedHashMap<String, Field> fieldvalues;
		public final Struct record;
		public final  HttpCall call;
		public Instance(HttpCall call, Struct record) {
			this.call=call;
			this.record=record;
			if (record==null)
				this.fieldvalues=fields;
			else {
				this.fieldvalues= new LinkedHashMap<String,Field>();
				for (Field f: fields.values()) {
					System.out.println(f+" "+f.name+" "+f.field);
					System.out.println("record:"+record);
					fieldvalues.put(f.name, new Field(f.field,f.field.getObjectValue(record)));
				}
			}
		}
		public GenericForm form() { return GenericForm.this;}
		public Collection<Field> fields() { return fieldvalues.values(); }
		@Override public Iterable<String> fieldNames() { return fieldvalues.keySet(); }
		@Override public Object getDirectFieldValue(String name) { return fieldvalues.get(name); }
		public void output(CompiledTemplate templ) { call.output(toString(templ)); }
		public String toString(CompiledTemplate templ) {
			TemplateData context = new TemplateData(call);
			context.add("form", this);
			return templ.toString(context); 
		}
	}
	
	public class Field extends ReflectStruct {
		public final Schema<?>.Field field;
		public final String name;
		public final Object value;
		public Field(Schema<?>.Field field) { this(field,null); }
		public Field(Schema<?>.Field field, Object value) {
			field.getClass();
			this.field=field;
			this.name=field.getName();
			this.value=value;;
		}
	}
}
