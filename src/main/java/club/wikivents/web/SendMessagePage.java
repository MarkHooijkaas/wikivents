package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;

public class SendMessagePage extends WikiventsPage {
	public SendMessagePage(WikiventsSite site) { super(site); }

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, data, call.getTheme().sendMessage); }
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
				String replyTo='"'+call.user.username+"\" <"+call.user.email+">";
				User toUser=model.usernameIndex.get(formdata.to.value);
				String to=toUser.email;
				String subject=formdata.subject.value;
				String message=formdata.message.value;
				site.emailer.send(replyTo, to, null, null, subject, message);
			}
			formdata.handle();
		}
	}
}