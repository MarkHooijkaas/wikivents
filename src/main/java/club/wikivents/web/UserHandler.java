package club.wikivents.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;
import org.kisst.util.PasswordEncryption;

import club.wikivents.model.Event;
import club.wikivents.model.User;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class UserHandler extends WikiventsActionHandler<User> {
	private final ResourceHandler uploads = new ResourceHandler(null, new File("data/uploads/"));
	public UserHandler(WikiventsSite site) { super(site, site.model.users); }

	@Override protected User findRecord(String id) {
		User result;
		if (id.startsWith(":"))
			result= model.users.read(id.substring(1));
		else
			result= model.usernameIndex.get(id);
		if (result!=null)
			CallInfo.instance.get().data=result.username;
		return result;
	}

	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().userList, model.users);
	}

	@NeedsNoAuthorization
	public void view(WikiventsCall call, User user) {
		call.output(call.getTheme().userShow, user);
	}
	@NeedsNoAuthorization
	public void viewAvatar(WikiventsCall call, User user) {
		uploads.handle(call, user._id+"/avatar.jpg");
	}
	@NeedsNoAuthorization
	public void viewUploaded(WikiventsCall call, User user, String subpath) {
		System.out.println(subpath);
		uploads.handle(call, user._id+"/"+subpath);
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

	
	@NeedsNoAuthentication
	public void viewValidateEmail(WikiventsCall call, User u) {
		String token = call.request.getParameter("token");
		if (token!=null && token.equals(u.passwordSalt)) {
			if (call.user==null && ! u.emailValidated)
				call.setCookie(u._id);
			call.model.users.updateField(u, User.schema.emailValidated, true);
		}
		call.redirect("/user/"+u.username);
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
	public void viewSetPassword(WikiventsCall call, User user) {
		String token = call.request.getParameter("token");
		if (user==null || token==null || token.equals("UNKNOWN_FIELD") || ! token.equals(user.passwordResetToken )) {
			call.sendError(403, "Not authorized");
			return;
		}
		SetPasswordForm formdata = new SetPasswordForm(call);
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
	
	@SuppressWarnings("unused")
	private boolean checkCaptcha(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(site.recaptchaPrivateKey);

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
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
		boolean valid = formdata.isValid();
		//valid = valid && checkCaptcha(call.request);
		
		if (valid) {
			String salt = PasswordEncryption.createSaltString();
			String pw = PasswordEncryption.encryptPassword(formdata.password.value, salt);
			User u = new User(call.model,new MultiStruct(
				formdata.record, new HashStruct()
				.add(User.schema.passwordSalt,  salt)
				.add(User.schema.encryptedPassword, pw)
			));
			model.users.create(u);
			String url= call.getTopUrl()+"/user/"+u.username+"?view=validateEmail&token="+u.passwordSalt;
			TemplateData context=call.createTemplateData();
			context.add("user", u);
			context.add("url", url);
			String message = call.getTheme().userRegisterSucces.toString(context);
			u.sendSystemMail("Welkom bij Wikivents: valideer dit mail adres", message);
			call.setCookie(u._id);
			call.redirect("/user/"+u.username);
		}
		else
			formdata.handle();
	}

	@NeedsNoAuthentication
	public void handleSetPassword(WikiventsCall call, User user) {
		SetPasswordForm formdata = new SetPasswordForm(call);
		if (formdata.isValid()) {
			String pw = formdata.password.value;
			String pw2 = formdata.passwordCheck.value;
			if (pw==null || ! pw.equals(pw2))
				formdata.showForm();
			else {
				user.changePassword(pw);
				call.setCookie(user._id);
				call.redirect("/user/"+user.username);
			}
		}
		else
			formdata.handle();
	}

	public void handleRemoveEmailValidationNeeded(WikiventsCall call, User u) {
		if (call.user.isAdmin)
			table.updateField(u, User.schema.emailValidated, true);
	}

	public void handleRemoveMessage(WikiventsCall call, User u) {
		//String message=call.request.getParameter("message");
		table.updateField(u, User.schema.message, "");
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
	public class RegisterForm extends HttpFormData {
		public RegisterForm(WikiventsCall call) { super(call, call.getTheme().userRegister);
			this.username = new InputField(User.schema.username, new UniqueKeyIndexValidator<User>(call.model.usernameIndex) );
			this.email    = new InputField(User.schema.email, this::validateEmail, new UniqueKeyIndexValidator<User>(call.model.emailIndex) );
		}
		public final InputField username;
		public final InputField email;
		public final InputField city     = new InputField(User.schema.city);
		public final InputField password = new InputField("password");
		public final InputField passwordCheck = new InputField("passwordCheck");
		@Override public boolean isValid() { 
			return 
				super.isValid() && 
				password.value!=null &&
				password.value.equals(passwordCheck.value);
				//&& checkCaptcha(call.request); 
		}

	}
	public static class SetPasswordForm extends HttpFormData {
		public SetPasswordForm(WikiventsCall call) { super(call, call.getTheme().userSetPassword); }
		public final InputField password = new InputField("password");
		public final InputField passwordCheck = new InputField("passwordCheck");
	}

	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		event.addLike(model, call.user);
	}
	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		event.removeLike(model,call.user);
	}

	
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
	public void handleUploadAvatar(WikiventsCall call, User u) {
		if (call.request.getContentType() != null && call.request.getContentType().startsWith("multipart/form-data"))
			call.baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		OutputStream out = null;
		InputStream filecontent = null;
		System.out.println("START");
		try {
			final Part filePart = call.request.getPart("avatar");
			final String fileName = getFileName(filePart); // "testje.img";
			File uploadDir = new File("data/uploads/"+u._id);
			if (! uploadDir.exists())
				uploadDir.mkdirs();
			out = new FileOutputStream(new File(uploadDir, fileName));
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			//writer.println("New file " + fileName + " created at " + path);
			//LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", 
			System.out.println("OK");
			table.updateField(u, User.schema.avatarUrl, "/user/:"+u._id+"/uploaded/"+fileName);
		} 
		catch (IOException e) { throw new RuntimeException(e); }
		catch (ServletException e) { throw new RuntimeException(e); }
		finally {
			try {
				if (out != null) out.close();
				if (filecontent != null)  filecontent.close();
			} 
			catch (IOException e) { throw new RuntimeException(e); }
		}
		call.redirect("/user/"+u.username+"?inline-edit=true");
	}

	private String getFileName(final Part part) {
	    //final String partHeader = part.getHeader("content-disposition");
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	
}

