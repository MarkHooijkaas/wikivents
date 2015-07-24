package org.kisst.http4j.handlebar;

import java.util.Collection;
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

import com.github.jknack.handlebars.Handlebars.SafeString;

public abstract class GenericForm implements HttpForm {
	private final CompiledTemplate defaultShowFieldTemplate;
	private final CompiledTemplate defaultEditFieldTemplate;
	private final TemplateEngine engine;
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<String,Field>();
	private final CompiledTemplate showTemplate;
	private final CompiledTemplate editTemplate;
	private final String prefix;

	public GenericForm(TemplateEngine engine) { this(engine,"");}
	public GenericForm(TemplateEngine engine, String prefix) {
		this.engine=engine;
		this.prefix=prefix;
		this.defaultShowFieldTemplate=engine.compile(
				engine.compileInline("<tr><th>{{field.label}}</th><td>{{value}}</td></tr>\n"),
				prefix+"field.show",
				"form/field.show");
		this.defaultEditFieldTemplate=engine.compile(
				engine.compileInline("<tr><th>{{field.label}}</hd><td><input type=\"{{field.type}}\" name=\"{{field.name}}\" id=\"{{field.name}}\" value=\"{{value}}\"/></td></tr>\n"),
				prefix+"field.edit",
				"form/field.edit");
		this.showTemplate=engine.compile(
				engine.compileInline("{{>header}} <table>{{#each fields}} {{&show}} {{/each}}</table> {{>footer}}"),
				prefix+"show",
				"form/show");
		this.editTemplate=engine.compile(
				engine.compileInline("{{>header}} <form><table>{{#each fields}} {{&edit}} {{/each}}</table></form> {{>footer}}"),
				prefix+"edit",
				"form/edit");
	}
	public Iterator<Field> fields() { return fields.values().iterator(); }

	@Override public void showViewPage(HttpCall call, Struct data) {
		TemplateData context = new TemplateData(call);
		context.add("form", new Instance(data));
		call.output(showTemplate.toString(context)); 
	}
	@Override public void showEditPage(HttpCall call, Struct data) {
		TemplateData context = new TemplateData(call);
		context.add("form", new Instance(data));
		call.output(editTemplate.toString(context)); 
	}

	protected void addAllFields() { addAllFields(this.getClass());	}
	
	private void addAllFields(Class<?> cls) {
		List<java.lang.reflect.Field> fs = ReflectionUtil.getAllDeclaredFieldsOfType(this.getClass(), Field.class);
		for (java.lang.reflect.Field f: fs) {
			fields.put(f.getName(), (Field) ReflectionUtil.getFieldValue(this,f.getName()));
		}
	}

	
	
	public class Instance implements Struct {
		private final LinkedHashMap<String,FieldValue> fieldvalues=new LinkedHashMap<String,FieldValue>(fields.size());
		public class FieldValue {
			public final Field field;
			public FieldValue(Field f) { this.field=f;}
			public String value() { return field.value(record);}
			public SafeString edit() { return new SafeString(field.templateEdit.toString(new TemplateData(this)));}
			public SafeString show() { return new SafeString(field.templateShow.toString(new TemplateData(this))); }
		}
		public final Struct record;
		public Instance(Struct record) { 
			this.record=record;
			for (Field f: fields.values())
				fieldvalues.put(f.name, new FieldValue(f));
		}
		public GenericForm form() { return GenericForm.this;}
		public Collection<FieldValue> fields() { return fieldvalues.values(); }
		@Override public Iterable<String> fieldNames() { return fieldvalues.keySet(); }
		@Override public Object getDirectFieldValue(String name) { return fieldvalues.get(name); }
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
			this.templateShow= engine.compile(
				defaultShowFieldTemplate,
				prefix+"field.show."+getClass().getSimpleName(),
				"form/field.show."+getClass().getSimpleName()
			);
			this.templateEdit= engine.compile(
				defaultEditFieldTemplate,
				prefix+"field.edit."+getClass().getSimpleName(),
				"form/field.edit."+getClass().getSimpleName()
			);
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
