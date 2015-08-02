package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class UserForm extends WikiventsThing {
	public UserForm(WikiventsSite site) { super(site); }
	
	private final CompiledTemplate template=engine.compileTemplate("user/edit");
	
	public class Form extends HttpFormData {
		public Form(HttpCall call, Struct data) { super(call, data, template); }
		public final InputField username = new InputField("username");
		public final InputField  email   = new InputField("email", this::validateEmail);
		
		@Override public String successUrl() {
			if (call.userid==null)
				return "/home"; // TODO: redirect a new user to a password screen
			else
				return "show/"+call.userid;
		}
	}

	
	public void handleCreate(HttpCall call, String subPath) {
		Form formdata = new Form(call,null);
		if (call.isGet()) 
			formdata.showForm();
		else {
			if (formdata.isValid())
				model.users.create(new User(model, formdata.record));
			formdata.handle();
		}
	}
	
	public void handleEdit(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		User oldRecord = model.users.read(subPath);
		Form formdata = new Form(call,null);
		
		if (! oldRecord.mayBeChangedBy(call.user))
			formdata.error("Not authorized");
		
		if (call.isGet())
			new Form(call,oldRecord).showForm();
		else {
			if (formdata.isValid()) {
				User newRecord=oldRecord.modified(model, formdata.record);
				System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
				model.users.update(oldRecord, newRecord);
			}
			formdata.handle();
		}
	}
}
