package org.kisst.http4j.handlebar;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpForm;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.Schema;
import org.kisst.item4j.struct.Struct;

public class GenericForm implements HttpForm {
	private final TemplateEngine site;
	private final TemplateEngine.CompiledTemplate template;
	private final LinkedHashMap<String, Field> fields = new LinkedHashMap<String,Field>();

	public GenericForm(TemplateEngine site) { this(site,"generic/form"); }
	public GenericForm(TemplateEngine site, String templateName) {
		this.site=site;
		this.template=this.site.compileTemplate(templateName);
	}
	public Iterator<Field> fields() { return fields.values().iterator(); }
	public String renderEdit(Struct data) { 
		//System.out.println("Rendering "+data);
		StringBuilder result = new StringBuilder();
		for (Field f: fields.values())
			result.append(f.renderEdit(data));
		return result.toString(); 
	}
	public String renderShow(Struct data) { 
		//System.out.println("Rendering "+data);
		StringBuilder result = new StringBuilder();
		for (Field f: fields.values())
			result.append(f.renderShow(data));
		return result.toString(); 
	}
	/*
	public void outputEdit(TemplateData context, Struct data, HttpServletResponse response) {
	}
	public void outputShow(TemplateData context, Struct data, HttpServletResponse response) {
		context.add("fields", renderShow(data));
		template.output(context, response); 
	}
*/
	@Override public void showViewPage(HttpCall call, Struct data) {
		
		TemplateData context = new TemplateData(this);
		context .add("fields", renderEdit(data));
		call.output(template.toString(context)); 
	}
	@Override
	public void showEditPage(HttpCall call, Struct data) {
		// TODO Auto-generated method stub
		
	}
	
	public GenericForm addField(Schema<?>.Field field, String label) { 
		fields.put(field.getName(), new TextField(field,label));
		return this;
	}
	public<T> GenericForm addEmailField(Schema<?>.Field field, String label) { 
		fields.put(field.getName(), new EmailField(field,label));
		return this;
	}
	public<T> GenericForm addPasswordField(Schema<?>.Field field, String label) { 
		fields.put(field.getName(), new PasswordField(field,label));
		return this;
	}
	public<T> GenericForm addTextAreaField(Schema<?>.Field field, String label, int rows) { 
		fields.put(field.getName(), new TextAreaField(field,label, rows));
		return this;
	}
	public<T> GenericForm addDateField(Schema<?>.Field field, String label) {
		fields.put(field.getName(), new DateField(field,label));
		return this;
	}

	
	
	public class Field {
		private final CompiledTemplate templateEdit;
		//private final CompiledTemplate templateShow;
		public final Schema<?>.Field field;
		public final String label;
		public final String name;
		public Field(Schema<?>.Field field, String label) {
			this.field=field;
			this.name=field.getName();
			this.label=label;
			this.templateEdit= site.compileTemplate("generic/form."+getClass().getSimpleName() );
			//this.templateShow= site.compileTemplate("generic/show."+getClass().getSimpleName() );
		}
		public String renderEdit(Struct data) {
			TemplateData context = new TemplateData(this);
			//System.out.println("Rendering "+this.getClass()+" field "+name+" from "+data);
			String value = field.getString(data);
			context.add("value", value);
			//System.out.println("Rendering field "+name+" to "+value);
			return templateEdit.toString(context);
		}
		public String renderShow(Struct data) {
			String value = field.getString(data);
			return "<tr><td>"+label+"</td><td>"+value+"</td></tr>";
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
		public TextAreaField(Schema<?>.Field field, String label,  int rows) { 
			super(field,label);
			this.rows=rows;
		}
	}
	public class DateField extends Field {
		public DateField(Schema<?>.Field field, String label) { super(field,label);}
	}
}
