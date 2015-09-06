package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class SendMessagePage extends WikiventsPage {
	public SendMessagePage(WikiventsSite site) { super(site); }

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().sendMessage, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().sendMessage); }

		public final InputField to = new InputField("to",this::notEmpty);
		public final InputField subject = new InputField("subject",this::notEmpty);
		public final InputField message= new InputField("message",this::notEmpty);
	}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		Form formdata = new Form(call);
		if (call.isGet()) 
			formdata.showForm();
		else {
			if (formdata.isValid()) {
				User toUser=model.usernameIndex.get(formdata.to.value);
				if (toUser==null)
					formdata.handle();
				else {
					String subject=formdata.subject.value;
					String message=formdata.message.value;
					toUser.sendMailFrom(call.user, subject, message);
					call.redirect("/user/"+toUser.username);
				}
			}
			formdata.handle();
		}
	}
}