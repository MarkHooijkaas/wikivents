package club.wikivents.web;

import javax.servlet.http.HttpServletRequest;

import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.User;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class UserHandler extends WikiventsActionHandler<User> {
	
	public UserHandler(WikiventsSite site) { super(site, site.model.users); }

	@Override protected User findRecord(String id) {
		if (id.startsWith(":"))
			return model.users.read(id.substring(1));
		return model.usernameIndex.get(id);
	}

	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().userList, model.users);
	}

	@NeedsNoAuthorization
	public void view(WikiventsCall call, User user) {
		call.output(call.getTheme().userShow, user);
	}
	public void viewEdit(WikiventsCall call, User u) {
		new Form(call,u).showForm();
	}
	public void handleEdit(WikiventsCall call, User oldRecord) {
		Form formdata = new Form(call,null);
		if (formdata.isValid()) 
			model.users.updateFields(oldRecord, formdata.record);
		formdata.handle();
	}
	
	public void viewLogout(WikiventsCall call) {
		call.output(call.getTheme().logout);
	}
	public void handleLogout(WikiventsCall call) {
		call.clearCookie();
		call.redirect("/");
	}
	
	@NeedsNoAuthentication
	public void viewRegister(WikiventsCall call) {
		RegisterForm formdata = new RegisterForm(call);
		formdata.showForm();
	}
	@NeedsNoAuthentication
	public void viewUsernameAvaliable(WikiventsCall call) {
		String username = call.request.getParameter("username");
		if (call.model.usernameIndex.get(username)==null)
			call.output("true");
		else
			call.output("false");
	}
	@NeedsNoAuthentication
	public void viewEmailAvaliable(WikiventsCall call) {
		String email = call.request.getParameter("email");
		if (call.model.emailIndex.get(email)==null)
			call.output("true");
		else
			call.output("false");
	}
	
	private boolean checkCaptcha(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(site.recaptchaPrivateKey);

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
		System.out.println("KEY:"+site.recaptchaPrivateKey);
		System.out.println("challenge:"+challenge);
		System.out.println("RESPONSE:"+uresponse);
		System.out.println("ADDR:"+remoteAddr);

		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

		if (reCaptchaResponse.isValid()) {
			System.out.println("Answer was entered correctly!");
			return true;
		} 
		else {
			System.out.println("Answer is wrong");
			return false;
		}

	}
	
	@NeedsNoAuthentication
	public void handleRegister(WikiventsCall call) {
		RegisterForm formdata = new RegisterForm(call);
	
		if (! checkCaptcha(call.request))
			return;

		if (formdata.isValid()) {
			String pw = formdata.password.value;
			String pw2 = formdata.passwordCheck.value;
			if (pw==null || ! pw.equals(pw2))
				formdata.showForm();
			else {
				User u = new User(call.model,formdata.record);
				model.users.create(u);
				u.changePassword(pw);
				call.setCookie(u._id);
				call.redirect("/user/"+u.username);
			}
		}
		else
			formdata.handle();

		/*
		 *
		 * 
		 * When your users submit the form where you integrated reCAPTCHA, you'll get as part of the payload a string with the name "g-recaptcha-response". In order to check whether Google has verified that user, send a POST request with these parameters:
URL: https://www.google.com/recaptcha/api/siteverify

secret (required)	6LeC9gwTAAAAALBihIUuY2D5jdox0_Il-nIzGVbM
response (required)	The value of 'g-recaptcha-response'.
remoteip	The end user's ip address.
		 */
	}
	

	public void handleChangeField(WikiventsCall call, User oldRecord) {
		String field=call.request.getParameter("field");
		String value=call.request.getParameter("value");
		table.updateField(oldRecord, table.getSchema().getField(field), value);
	}

	public void handleChangePassword(WikiventsCall call, User u) {
		String newPassword= call.request.getParameter("newPassword");
		String checkNewPassword= call.request.getParameter("checkNewPassword");
		if (! newPassword.equals(checkNewPassword))
			call.sendError(500, "supplied passwords do not match");
		else
			u.changePassword(newPassword);
	}
	
	public static class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, call.getTheme().userEdit, data); }
		public Form(WikiventsCall call) 			 { super(call, call.getTheme().userEdit); }
		public final InputField username = new InputField(User.schema.username);
		public final InputField email    = new InputField(User.schema.email, this::validateEmail);
		public final InputField city     = new InputField(User.schema.city);
		public final InputField avatarUrl= new InputField(User.schema.avatarUrl);
	}
	public static class RegisterForm extends HttpFormData {
		public RegisterForm(WikiventsCall call) { super(call, call.getTheme().userRegister);
			this.username = new InputField(User.schema.username, new UniqueKeyIndexValidator<User>(call.model.usernameIndex) );
			this.email    = new InputField(User.schema.email, this::validateEmail, new UniqueKeyIndexValidator<User>(call.model.emailIndex) );
		}
		public final InputField username;
		public final InputField email;
		public final InputField city     = new InputField(User.schema.city);
		public final InputField password = new InputField("password");
		public final InputField passwordCheck = new InputField("passwordCheck");
	}
}

