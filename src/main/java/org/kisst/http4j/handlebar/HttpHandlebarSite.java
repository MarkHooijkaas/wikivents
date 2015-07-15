package org.kisst.http4j.handlebar;



import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Context.Builder;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class HttpHandlebarSite {
	public final Struct props;
	public final boolean debug;
	
	public HttpHandlebarSite(Struct props) {
		this.props=props;
		this.debug=props.getBoolean("debug",true);
	}
	
	
	
	private final Handlebars handlebar=new Handlebars(new FileTemplateLoader("src/templates",".template"));
	public CompiledTemplate compileTemplate(String templateName) { return new CompiledTemplate(templateName); }
	private Template compile(String templateName) {
		try {
			return handlebar.compile(templateName);
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
		public String toString(TemplateData context) {
			Template tmpl = template;
			if (debug)
				tmpl=compile(name);
			try { 
			    return tmpl.apply(context.builder.build());
			} 
			catch (IOException e) { throw new RuntimeException(e);}
		}

		public void output(TemplateData context, HttpServletResponse response) {
			try { //TODO: use autoclosable (Writer out = response.getWriter())	{
				Writer out = response.getWriter();
			    out.append(toString(context));
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
