package org.kisst.http4j.handlebar;



import java.io.IOException;
import java.io.PrintStream;

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
	
	public TemplateEngine(Struct props) {
		this.props=props;
		this.loadDynamic=props.getBoolean("loadDynamic",false); // TODO: handlebars seems to load dynamically always
		String dir=props.getString("file.dir", null);
		System.out.println(props);
		ClassPathTemplateLoader cp = new ClassPathTemplateLoader("/templates/",".template");
		if (dir==null)
			this.handlebar=new Handlebars(cp);
		else
			this.handlebar=new Handlebars(new CompositeTemplateLoader(new FileTemplateLoader(dir,".template"),cp));
	}


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
		@Override public String toString() { return "Template("+name+")";}
		public String toString(TemplateData context) {
			Template tmpl = template;
			if (loadDynamic)
				tmpl=compile(name);
			try { 
			    return tmpl.apply(context.builder.build());
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
