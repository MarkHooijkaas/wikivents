package org.kisst.http4j.handlebar;



import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

import org.kisst.props4j.Props;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Context.Builder;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class TemplateEngine {
	public final Props props;
	public final boolean loadDynamic;
	private final Handlebars handlebar;
	//private final File dir;
	private final String postfix;
	private final String dirnames;
	
	public TemplateEngine(Props props) {
		this.props=props;
		this.loadDynamic=props.getBoolean("loadDynamic",false); // TODO: handlebars seems to load dynamically always
		String filedir = props.getString("file.dir", null);
		this.dirnames=filedir;
		this.postfix = props.getString("postfix", ".hbr");
		TemplateLoader cp = new ClassPathTemplateLoader("/templates/default/",postfix);
		if (filedir!=null) {
			String[] dirs = filedir.split(",");
			TemplateLoader[] loaders=new TemplateLoader[dirs.length+1];
			for (int i=0; i<dirs.length; i++)
				loaders[i]=new FileTemplateLoader(new File(dirs[i]),postfix);
			loaders[dirs.length]=cp;
			cp=new CompositeTemplateLoader(loaders);
		}
			this.handlebar=new Handlebars(cp)
					.with(new ConcurrentMapTemplateCache());;
	}

	@Override public String toString() { return "TemplateEngine("+dirnames+")"; }
	public <T> void registerHelper(String name, Helper<T> helper) { handlebar.registerHelper(name, helper); }
	public void registerHelpers(Object helpers) { handlebar.registerHelpers(helpers); }
	public <T> void registerUserHelpers(Class<T> cls, String path) {
		UserHelpers<T> h = new UserHelpers<T>(cls, path);
		handlebar.registerHelper("ifMayChange", h.new IfMayChangeHelper()); 
		handlebar.registerHelper("ifMayView",   h.new IfMayViewHelper());
		handlebar.registerHelper("ifInlineEdit",   h.new IfInlineEditHelper());
		handlebar.registerHelpers(h.new SimpleHelpers());
	}

	//public boolean exists(String templateName) { return new File(dir,templateName+postfix).exists(); } // TODO: handle classpath

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
			        FieldValueResolver.INSTANCE,
					StructValueResolver.INSTANCE,
					MapValueResolver.INSTANCE,
			        JavaBeanValueResolver.INSTANCE,
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
