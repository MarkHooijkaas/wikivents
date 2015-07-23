package org.kisst.http4j.handlebar;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpForm;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

public abstract class GenericForm implements HttpForm {
	private final CompiledTemplate defaultShowFieldTemplate;
	private final CompiledTemplate defaultEditFieldTemplate;
	private final TemplateEngine engine;
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<String,Field>();
	private final CompiledTemplate showTemplate;
	private final CompiledTemplate editTemplate;

	public GenericForm(TemplateEngine engine) {
		this.engine=engine;
		this.defaultShowFieldTemplate=engine.compile(
				engine.compileInline("<tr><th>{{field.label}}</th><td>{{value}}</td></tr>\n"),
				"form/field.show");
		this.defaultEditFieldTemplate=engine.compile(
				engine.compileInline("<tr><th>{{field.label}}</hd><td><input type=\"{{field.type}}\" name=\"{{field.name}}\" id=\"{{field.name}}\" value=\"{{value}}\"/></td></tr>\n"),
				"form/field.edit");
		this.showTemplate=engine.compile(
				engine.compileInline("{{>header}} <table>{{#each fields}} {{&show}} {{/each}}</table> {{>footer}}"),
				"form/show");
		this.editTemplate=engine.compile(
				engine.compileInline("{{>header}} <form><table>{{#each fields}} {{&edit}} {{/each}}</table></form> {{>footer}}"),
				"form/edit");
	}
	public Iterator<Field> fields() { return fields.values().iterator(); }

	@Override public void showViewPage(HttpCall call, Struct data) {
		System.out.println("Showing view page with template "+showTemplate);
		TemplateData context = new TemplateData(new Instance(data));
		call.output(showTemplate.toString(context)); 
	}
	@Override public void showEditPage(HttpCall call, Struct data) {
		System.out.println("Showing edit page with template "+editTemplate);
		TemplateData context = new TemplateData(new Instance(data));
		call.output(editTemplate.toString(context)); 
	}

	protected void addAllFields() { addAllFields(this.getClass());	}
	
	private void addAllFields(Class<?> cls) {
		List<java.lang.reflect.Field> fs = ReflectionUtil.getAllDeclaredFieldsOfType(this.getClass(), Field.class);
		for (java.lang.reflect.Field f: fs) {
			fields.put(f.getName(), (Field) ReflectionUtil.getFieldValue(this,f.getName()));
		}
	}

	
	
	public class Instance  {
		public class FieldValue {
			public final Field field;
			public FieldValue(Field f) { this.field=f;}
			public String value() { return field.value(data);}
			public String edit() { return field.templateEdit.toString(new TemplateData(this));}
			public String show() { return field.templateShow.toString(new TemplateData(this)); }
		}
		public final Struct data;
		public Instance(Struct data) { this.data=data;}
		public GenericForm form() { return GenericForm.this;}
		public FieldValue[] fields() {
			FieldValue[] result=new FieldValue[fields.size()];
			int i=0;
			for (Field f: fields.values())
				result[i++]=new FieldValue(f);
			return result;
		}
	}
	
	public class Field {
		public final CompiledTemplate templateShow;
		public final CompiledTemplate templateEdit;
		public final Schema<?>.Field field;
		public final String label;
		public final String name;
		public Field(Schema<?>.Field field, String label) {
			this.field=field;
			this.name=field.getName();
			this.label=label;
			this.templateShow= engine.compile(defaultShowFieldTemplate,"form/field.show."+getClass().getSimpleName() );
			this.templateEdit= engine.compile(defaultEditFieldTemplate,"form/field.edit."+getClass().getSimpleName() );
			System.out.println(this.name+",show="+this.templateShow+",edit="+this.templateEdit);
		}
		public String value(Struct data) { return field.getString(data); }
		public String type() { 
			String name=this.getClass().getSimpleName();
			if (name.endsWith("Field"))
				name=name.substring(0, name.length()-5);
			return name.toLowerCase();
		}
	}
	public class TextField extends Field {
		public TextField(Schema<?>.Field field, String label) { super(field,label); }
	}
	public class EmailField extends Field {
		public EmailField(Schema<?>.Field field, String label) { super(field,label); }
	}
	public class PasswordField extends Field {
		public PasswordField(Schema<?>.Field field, String label) { super(field,label); }
	}
	public class TextAreaField extends Field {
		public final int rows;
		public TextAreaField(Schema<?>.Field field, String label,  int rows) { super(field,label); this.rows=rows; }
	}
	public class DateField extends Field {
		public DateField(Schema<?>.Field field, String label) { super(field,label);}
	}
}
