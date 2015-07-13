package club.wikivents.web;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.props4j.Props;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;


public class TemplateServlet extends BaseServlet {
	//private static final Configuration cfg= new Configuration();;
	//static {
	//	//cfg.setDirectoryForTemplateLoading(new File("src/java"));
	//	cfg.setClassForTemplateLoading(TemplateServlet.class, "/");
	//	cfg.setObjectWrapper(new DefaultObjectWrapper());
	//}


	private final String templateName;
	private final Mustache template;
    private final MustacheFactory factory = new DefaultMustacheFactory(new File("src/templates"));
	private boolean debugMode=true;

	public TemplateServlet(Props props) {
		super(props);
		this.templateName=this.getClass().getSimpleName().replace('.', '/')+".template";
	    this.template = factory.compile(templateName);
	}

	public TemplateServlet(Props props, String templateName) {
		super(props);
		this.templateName=templateName;
	    this.template = factory.compile(templateName);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("handling");
		if (getUser(request, response)==null)
			return;
		try {
			HashMap<String, Object> root=new HashMap<String, Object>();
			//root.put("model", model);
			addContext(root);
			System.out.println("events "+root.get("events"));
			Mustache templ = this.template;
			if (debugMode)
				templ=new DefaultMustacheFactory(new File("src/templates")).compile(templateName);
			
			Writer out = response.getWriter();
		    templ.execute(out, root);
			out.flush();
		}
		catch (IOException e)  { throw new RuntimeException(e); }
	}


	protected void addContext(HashMap<String, Object> root) {}
}
