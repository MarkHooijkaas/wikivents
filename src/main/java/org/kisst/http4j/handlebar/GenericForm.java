package org.kisst.http4j.handlebar;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.handlebar.HttpHandlebarSite.CompiledTemplate;
import org.kisst.http4j.handlebar.HttpHandlebarSite.TemplateData;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public class GenericForm {
	private final HttpHandlebarSite site;
	private final HttpHandlebarSite.CompiledTemplate template;
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<String,Field>();

	public GenericForm(HttpHandlebarSite site) { this(site,"generic.form"); }
	public GenericForm(HttpHandlebarSite site, String templateName) {
		this.site=site;
		this.template=this.site.compileTemplate(templateName);
	}
	public Iterator<Field> fields() { return fields.values().iterator(); }
	public String render(Struct data) { 
		StringBuilder result = new StringBuilder();
		for (Field f: fields.values())
			result.append(f.render(data));
		return result.toString(); 
	}
	public void output(TemplateData context, Struct data, HttpServletResponse response) {
		context.add("fields", render(data));
		template.output(context, response); 
	}

	
	public<T> GenericForm addField(Schema.Field<T> field, String label) { 
		fields.put(field.getName(), new TextField(field,label));
		return this;
	}
	public<T> GenericForm addEmailField(Schema.Field<T> field, String label) { 
		fields.put(field.getName(), new EmailField(field,label));
		return this;
	}
	public<T> GenericForm addPasswordField(Schema.Field<T> field, String label) { 
		fields.put(field.getName(), new PasswordField(field,label));
		return this;
	}
	public<T> GenericForm addTextAreaField(Schema.Field<T> field, String label, int rows) { 
		fields.put(field.getName(), new TextAreaField(field,label, rows));
		return this;
	}
	public<T> GenericForm addDateField(Schema.Field<T> field, String label) {
		fields.put(field.getName(), new DateField(field,label));
		return this;
	}

	
	
	public class Field {
		private final CompiledTemplate template;
		public final Schema.Field<?> field;
		public final String label;
		public final String name;
		public Field(Schema.Field<?> field, String label) {
			this.field=field;
			this.name=field.getName();
			this.label=label;
			this.template= site.compileTemplate("generic.form."+getClass().getSimpleName() );
		}
		public String render(Struct data) {
			TemplateData context = new TemplateData(this);
			return template.toString(context);
		}
	}
	public class TextField extends Field {
		public TextField(Schema.Field<?> field, String label) { super(field,label); }
	}
	public class EmailField extends Field {
		public EmailField(Schema.Field<?> field, String label) { super(field,label); }
	}
	public class PasswordField extends Field {
		public PasswordField(Schema.Field<?> field, String label) { super(field,label); }
	}
	public class TextAreaField extends Field {
		public final int rows;
		public TextAreaField(Schema.Field<?> field, String label,  int rows) { 
			super(field,label);
			this.rows=rows;
		}
	}
	public class DateField extends Field {
		public DateField(Schema.Field<?> field, String label) { super(field,label);}
	}
}
