package org.kisst.http4j.handlebar;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

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

	
	public GenericForm addField(Schema.Field field, String label) { 
		fields.put(field.getName(), new TextField(field,label));
		return this;
	}
	public GenericForm addEmailField(Schema.Field field, String label) { 
		fields.put(field.getName(), new EmailField(field,label));
		return this;
	}
	public GenericForm addPasswordField(Schema.Field field, String label) { 
		fields.put(field.getName(), new PasswordField(field,label));
		return this;
	}
	public GenericForm addTextAreaField(Schema.Field field, String label, int rows) { 
		fields.put(field.getName(), new TextAreaField(field,label, rows));
		return this;
	}

	
	
	public class Field {
		public final Schema.Field field;
		public final String label;
		public final String type;
		public Field(Schema.Field field, String label, String type) {
			this.type=type;
			this.field=field;
			this.label=label;
		}
		public String render(Struct data) {
			String name=field.getName();
			String value=field.getStringValue(data,"");
			StringBuilder result = new StringBuilder();
			result.append("<div class=\"form-group\">");
			result.append("   <label for=\""+name+"\">"+label+"</label>");
			result.append("<input type=\""+type+"\" class=\"form-control\" id=\""+name+"\" name=\""+name+"\"+ value=\""+value+"\">");
			result.append("</div>");
			return result.toString();
		}
	}
	public class TextField extends Field {
		public TextField(Schema.Field field, String label) { super(field,label, "text"); }
	}
	public class EmailField extends Field {
		public EmailField(Schema.Field field, String label) { super(field,label, "email"); }
	}
	public class PasswordField extends Field {
		public PasswordField(Schema.Field field, String label) { super(field,label, "password"); }
	}
	public class TextAreaField extends Field {
		private final int rows;
		public TextAreaField(Schema.Field field, String label,  int rows) { 
			super(field,label, "textarea");
			this.rows=rows;
		}
		@Override public String render(Struct data) {
			String name=field.getName();
			String value=field.getStringValue(data,"");
			StringBuilder result = new StringBuilder();
			result.append("<div class=\"form-group\">");
			result.append("   <label for=\""+name+"\">"+label+"</label>");
			result.append("<textarea class=\"form-control\" rows=\""+rows+"\" id=\""+name+"\" name=\""+name+"\">"+value+"</textarea>");
			result.append("</div>");
			return result.toString();
		}
	}
}
