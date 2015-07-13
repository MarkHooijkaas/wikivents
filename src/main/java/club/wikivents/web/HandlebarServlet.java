package club.wikivents.web;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.props4j.Props;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Context.Builder;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;


public class HandlebarServlet extends BaseServlet {

	private final String templateName;
	private boolean debugMode=true;
	private final Handlebars handlebar=new Handlebars(new FileTemplateLoader("src/templates",".template"));
	private final Template template;
	private final Object root;

	public HandlebarServlet(Object root, Props props) {
		super(props);
		this.root=root;
		this.templateName=this.getClass().getSimpleName();
		this.template=compile();
	}

	public HandlebarServlet(Object root, Props props, String templateName) {
		super(props);
		this.root=root;
		this.templateName=templateName;
	    this.template = compile();
	}
	
	private Template compile() {
		//handlebar.wi		
		try {
			return handlebar.compile(templateName);
		}
		catch (IOException e) { throw new RuntimeException(e);}
	}

	
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("handling");
		if (getUser(request, response)==null)
			return;
		Template templ = this.template;
		if (debugMode)
			templ=compile();
		Builder context = Context.newBuilder(root)
				.resolver(
					StructValueResolver.INSTANCE,
					MapValueResolver.INSTANCE,
			        JavaBeanValueResolver.INSTANCE,
			        FieldValueResolver.INSTANCE,
			        MethodValueResolver.INSTANCE
			    );
		context= addContext(context);
		try (Writer out = response.getWriter())	{
			Context ctx = context.build();
			System.out.println("user.username="+ctx.get("user.username"));
			System.out.println("user    ="+ctx.get("user"));
			System.out.println("events  ="+ctx.get("events"));
			System.out.println("this    ="+ctx.get("this"));
			

		    out.append(templ.apply(ctx));
			out.flush();
		}
		catch (IOException e)  { throw new RuntimeException(e); }
	}


	protected Builder addContext(Builder context) { return context;}
}
