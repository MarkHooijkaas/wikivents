package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.User;

public class SendMessagePage extends WikiventsPage {
	private final static Logger maillogger=LoggerFactory.getLogger("mail");

	public SendMessagePage(WikiventsSite site) { super(site); }

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().sendMessage, data); }
		public Form(WikiventsCall call) { super(call, call.getTheme().sendMessage); }

		public final ToField to = new ToField("to",this::notEmpty);
		public final InputField subject = new InputField("subject");
		public final InputField message= new InputField("message",this::notEmpty);
		public final InputField returnTo = new InputField("returnTo");
		//public final InputField copyToSender= new InputField("copyToSender",this::notEmpty);;
		
		public class ToField extends InputField {
			public ToField(String name, Validator... validators) {
				super(name, (record==null) ? null : stripCommas(record, name), validators);
			}
		}

	}
	private static String stripCommas(Struct rec, String name) {
		if (rec==null)
			return null;
		String result = Item.asString(rec.getDirectFieldValue(name));
		String[] parts = result.split("[,]");
		result="";
		String sep="";
		for (String part : parts) {
			if (part.trim().length()>0) {
				result+=sep+part;
				sep=", ";
			}
		}
		return result;
	}


	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		if (!call.user.maySendMail())
			throw new RuntimeException("User not allowed to send email");
		Form formdata = new Form(call);
		if (call.isGet()) 
			formdata.showForm();
		else {
			if (formdata.isValid() && call.user.emailValidated) {
				String[] usernames=formdata.to.value.split("[,]");
				User[] toUser=new User[usernames.length];
				int i=0;
				for (String name: usernames) {
					toUser[i]=model.usernameIndex.get(name.trim());
					if (toUser[i]==null) {
						formdata.handle();
						return;
					}
					i++;
				}
				String subject=formdata.subject.value;
				String message=formdata.message.value;
				//System.out.println(formdata.copyToSender.value);
				boolean copyToSender=false;//true;//"true".equals(formdata.copyToSender.value);
				TemplateData context = call.createTemplateData();
				context.add("message", message);
				context.add("from", call.user);
				context.add("subject", formdata.subject.value);
				for (User u: toUser) {
					boolean needsWarning=!call.user.mayRecommend();
					if (call.user.isRecommendedBy(u))
						needsWarning=false;
					maillogger.info("Sending mail from {} ({}) to {} : {} ",call.user.username, call.request.getRemoteAddr(), u.username, subject);
					context.add("to", u);
					String body=message;
					if (needsWarning)
						body=call.getTheme().mail.toString(context);
					u.sendMailFrom(call.user, subject, body, copyToSender);
				}
				call.redirect(formdata.returnTo.value);
			}
			formdata.handle();
		}
	}
}