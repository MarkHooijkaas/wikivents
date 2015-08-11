package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class UserForm extends WikiventsThing {
	public UserForm(WikiventsSite site) { super(site); }
	
	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().userEdit, data); }
		public final InputField username = new InputField(User.schema.username);
		public final InputField email    = new InputField(User.schema.email, this::validateEmail);
		public final InputField city     = new InputField(User.schema.city);
		public final InputField avatarUrl= new InputField(User.schema.avatarUrl);
		
		@Override public String successUrl() {
			WikiventsCall wcall = WikiventsCall.of(call, model);
			if (call.userid==null)
				return "/home"; // TODO: redirect a new user to a password screen
			else
				return "/user/show/"+wcall.user.username;
		}
	}

	
	public void handleCreate(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
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
			if (formdata.isValid()) 
				model.users.updateFields(oldRecord, formdata.record);
			formdata.handle();
		}
	}
}
