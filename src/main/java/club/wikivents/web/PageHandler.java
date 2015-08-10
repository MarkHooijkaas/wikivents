package club.wikivents.web;

import java.lang.reflect.Method;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.ReflectionUtil;

import club.wikivents.model.Page;
import club.wikivents.model.User;

public class PageHandler extends WikiventsPage {
	public PageHandler(WikiventsSite site) { super(site);	}
	public final UserForm crud=new UserForm(site);


	private static final Class<?>[] signature = new Class<?>[] { HttpCall.class, String.class, Page.class };

	@Override public void handle(HttpCall call, String subPath) {
		String id="";
		String action="default";
		String remainder="";
		if (subPath!=null) {
			String[] elements = subPath.split("//*");
			id=elements[0];
			if (elements.length>1)
				action=elements[1].trim();
			for (int i=2; i<elements.length; i++)
				remainder+="/"+elements[i].trim();
			if (remainder.length()>0)
				remainder=remainder.substring(1);
		}

		Page record=null;
		if (id.equals(":new")) {
			action="new";
			record=null;
		}
		else if (id.startsWith(":"))
			record = model.pages.read(id.substring(1));
		else
			record = model.pageIndex.get(id);
		
		String methodName=action+"HandleGet";
		if ("POST".equals(call.request.getMethod().toUpperCase()))
			methodName=action+"HandlePost";
		System.out.println("id="+id+", page="+record+", action="+action+", remainder="+remainder+", method="+methodName);
		Method method = ReflectionUtil.getMethod(this.getClass(), methodName, signature);
		if (method==null)
			throw new RuntimeException("Unknown method "+methodName);
		ReflectionUtil.invoke(this, method, new Object[]{ call, remainder, record}); 
	}

	public void defaultHandleGet(HttpCall httpcall, String subPath, Page page) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		if ("true".equals(call.request.getParameter("edit")))
			new Form(call,page).showForm();
		else {
			TemplateData context = call.createTemplateData().add("page", page);
			call.output(call.getTheme().pageShow,context);
		}
	}

	public void newHandleGet(HttpCall httpcall, String subPath, Page page) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		new Form(call,page).showForm();
	}
	public void newHandlePost(HttpCall httpcall, String subPath, Page page) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		model.pages.create(new Page(model, new HttpRequestStruct(call.request)));
	}

	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, data, call.getTheme().pageEdit); }
		public final InputField name    = new InputField(Page.schema.name);
		public final InputField content = new InputField(Page.schema.content);
	}

	public void defaultHandlePost(HttpCall httpcall, String subPath, Page page) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		Form formdata = new Form(call,null);
		
	
		if (formdata.isValid()) {
			if (page==null) {
				MultiStruct data = new MultiStruct(
					new SingleItemStruct(Page.schema.owners.name, ImmutableSequence.of(User.Ref.class, new User.Ref(model, call.user._id))),
					formdata.record
				);
				model.pages.create(new Page(model, data));
				// alternative model.pages.updateField(oldValue, field, newValue);
			}
			else {
				if (page.mayBeChangedBy(call.user))
					model.pages.updateFields(page, formdata.record);
				else
					formdata.error("Not authorized");
			}
		}
		//formdata.handle();
	}
	
}

