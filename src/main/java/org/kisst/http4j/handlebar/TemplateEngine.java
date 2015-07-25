package org.kisst.http4j.handlebar;



import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Context.Builder;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class TemplateEngine {
	public final Struct props;
	public final boolean loadDynamic;
	private final Handlebars handlebar;
	private final File dir;
	private final String postfix;
	
	public TemplateEngine(Struct props) {
		this.props=props;
		this.loadDynamic=props.getBoolean("loadDynamic",false); // TODO: handlebars seems to load dynamically always
		String filedir = props.getString("file.dir", null);
		this.postfix = props.getString("postfix", ".hbr");
		if (filedir==null)
			this.dir=null;
		else
			this.dir=new File(filedir);
		System.out.println(props);
		ClassPathTemplateLoader cp = new ClassPathTemplateLoader("/templates/",postfix);
		if (dir==null)
			this.handlebar=new Handlebars(cp);
		else
			this.handlebar=new Handlebars(new CompositeTemplateLoader(new FileTemplateLoader(dir,postfix),cp));
	}

	public void registerHelpers(Object helpers) {
		handlebar.registerHelpers(helpers);
	}
	public boolean exists(String templateName) { return new File(dir,templateName+postfix).exists(); } // TODO: handle classpath

	public CompiledTemplate compile(CompiledTemplate defaultTemplate, String ... names) {
		for (String name:names) {
			if (exists(name)) {
				System.out.println("Compiling "+name);
				return compileTemplate(name);
			}
		}
		return defaultTemplate;
	}

	public CompiledTemplate compileTemplate(String templateName) { return new CompiledTemplate(templateName); }
	private Template compile(String templateName) {
		try {
			return handlebar.compile(templateName);
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}
	public CompiledTemplate compileInline(String template) {
		try {
			return new CompiledTemplate(handlebar.compileInline(template));
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}

	public class CompiledTemplate {
		private final Template template;
		private final String name;
		public CompiledTemplate(String name) {
			this.name=name;
			this.template=compile(name);
		}
		public CompiledTemplate(Template template) {
			this.name=null;
			this.template=template;
		}
		@Override public String toString() { return "Template("+name+")";}
		public String toString(TemplateData context) {
			Template tmpl = template;
			if (loadDynamic && name!=null)
				tmpl=compile(name);
			try { 
			    return tmpl.apply(context.builder.build());
			} 
			catch (IOException e) { throw new RuntimeException(e);}
		}
		public void output(TemplateData context, Writer writer) {
			Template tmpl = template;
			if (loadDynamic && name!=null)
				tmpl=compile(name);
			try { 
			    tmpl.apply(context.builder.build(), writer);
			} 
			catch (IOException e) { throw new RuntimeException(e);}
		}
	}

	public static class TemplateData {
		private final Builder builder;
		public TemplateData(Object root) {
			this.builder= Context.newBuilder(root)
				.resolver(
					StructValueResolver.INSTANCE,
					MapValueResolver.INSTANCE,
			        JavaBeanValueResolver.INSTANCE,
			        FieldValueResolver.INSTANCE,
			        MethodValueResolver.INSTANCE
			    );
		}
		public TemplateData add(String name, Object value) { builder.combine(name, value); return this;}
		public void write(PrintStream out) {
			Context ctx = builder.build();
			out.println(ctx.toString());
		}
	}
}
