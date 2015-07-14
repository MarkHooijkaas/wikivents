package org.kisst.servlet4j;



import java.io.IOException;
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

public class HttpHandlebarSite extends HttpServer {
	public final Struct props;
	public final boolean debug;
	
	public HttpHandlebarSite(Struct props) {
		super(props);
		this.props=props;
		this.debug=props.getBoolean("debug",false);
	}
	
	
	
	private final Handlebars handlebar=new Handlebars(new FileTemplateLoader("src/templates",".template"));
	public Template compile(String templateName) {
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
		public void output(TemplateData context, HttpServletResponse response) {
			Template tmpl = template;
			if (debug)
				tmpl=compile(name);
			try (Writer out = response.getWriter())	{
			    out.append(tmpl.apply(context.builder.build()));
			} 
			catch (IOException e) { throw new RuntimeException(e);}
		}
	}

	public class TemplateData {
		private final Builder builder = Context.newBuilder(this)
				.resolver(
					StructValueResolver.INSTANCE,
					MapValueResolver.INSTANCE,
			        JavaBeanValueResolver.INSTANCE,
			        FieldValueResolver.INSTANCE,
			        MethodValueResolver.INSTANCE
			    );
		public void add(String name, Object value) { builder.combine(name, value); }
	}
}
