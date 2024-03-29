package club.wikivents.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.kisst.util.StringUtil;

import club.wikivents.model.Tag;
import club.wikivents.model.User;
import club.wikivents.model.UserCommands.RemoveRecommendationCommand;
import club.wikivents.model.UserItem;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class UserHandler extends WikiventsActionHandler<User> {
	private final ResourceHandler uploads = new ResourceHandler(null, new File("data/uploads/"));
	public final User.Schema schema;
	public UserHandler(WikiventsSite site) { super(site, site.model.users, site.model.usernameIndex); this.schema=User.schema; }

	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().userList, model.users);
	}
	public void listExport(WikiventsCall call) {
		if (call.isAuthenticated() && call.user.isAdmin) {
			ArrayList<User> users = new ArrayList<User>();
			for (User u : model.users) {
				if (u.karmaNotNegative() && u.canReceiveMail() && u.emailValidated)
					users.add(u);
			}
			users.sort((User u1, User u2) -> u1.creationDate.compareTo(u2.creationDate));
			StringBuilder out = new StringBuilder();
			out.append("EMAIL	USERNAME	OPTIN_DATE	WEEKLY_ACT_MAIL	MONTHLY_NEWS_MAIL	CITY	ACT_TOTAL	UNSUB_TOKEN\n");
			for (User u : users) {
				out.append(u.email).append('\t');
				out.append(u.username).append('\t');
				out.append(u.creationDate.toString().substring(0, 10)).append('\t');
				out.append(u.subscribeWeeklyActivities && ! u.archived).append('\t');
				out.append(u.subscribeMonthlyMail && ! u.archived).append('\t');
				out.append(u.city).append('\t');

				out.append(u.allEvents().size()).append('\t');
				out.append(u.unsubscribeToken()).append('\n');
			}
			call.output(out.toString());
		}
		else
			call.redirect("/");
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
		uploads.handle(call, user._id+"/"+subpath);
	}

	@Override public void viewHistory(WikiventsCall call, User record) {
		if (record.mayBeChangedBy(call.user))
			super.viewHistory(call, record);
		else
			call.throwUnauthorized("Not authorized to view history of other user");
	}

	
	public void viewEdit(WikiventsCall call, User u) {
		new Form(call,u).showForm();
	}
	public void handleEdit(WikiventsCall call, User oldRecord) {
		Form formdata = new Form(call,null);
		if (formdata.isValid()) 
			table.update(oldRecord, oldRecord.changeFields(formdata.record));
		formdata.handle();
	}

	
	@NeedsNoAuthentication
	public void viewValidateEmail(WikiventsCall call, User u) {
		String token = call.request.getParameter("token");
		if (token!=null && token.equals(u.passwordSalt)) {
			if (call.user==null && ! u.emailValidated)
				call.setUserCookie(u);
			table.update(u, u.changeField(schema.emailValidated, true));
		}
		call.redirect("/user/"+u.username);
	}

	public void viewLogout(WikiventsCall call) {
		call.output(call.getTheme().logout);
	}
	public void handleLogout(WikiventsCall call) {
		call.clearUserCookie();
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
	
	@NeedsNoAuthentication
	public void handleRegister(WikiventsCall call) {
		RegisterForm formdata = new RegisterForm(call);
		boolean valid = formdata.isValid();
		if (valid) {
			String salt = PasswordEncryption.generateSalt();
			String pw = PasswordEncryption.encryptPassword(formdata.password.value, salt);
			User u = new User(call.model,new MultiStruct(
				new HashStruct()
					.add(schema.passwordSalt,  salt)
					.add(schema.encryptedPassword, pw)
					.add(schema.isAdmin, "false")
				,formdata.record 
			));
			CallInfo.instance.get().data=u.username;
			model.users.create(u);
			//String token = new SecureToken(call.model, u._id).getToken();
			String url= call.getTopUrl()+"/user/"+u.username+"?view=validateEmail&token="+u.passwordSalt;//+"&loginToken="+token;
			TemplateData context=call.createTemplateData();
			context.add("user", u);
			context.add("url", url);
			String message = call.getTheme().userRegisterSucces.toString(context);
			u.sendSystemMail("Welkom bij Wikivents "+u.username+": valideer dit mail adres", message);
			call.setUserCookie(u);
			call.redirect("/user/"+u.username);
		}
		else
			formdata.handle();
	}

	
	
	private void changePassword(User user, String newPassword) {
		table.update(user, user.changePassword(newPassword));
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
				changePassword(user,pw);
				call.setUserCookie(user);
				call.redirect("/user/"+user.username);
			}
		}
		else
			formdata.handle();
	}

	public void handleRemoveEmailValidationNeeded(WikiventsCall call, User u) {
		if (call.user.isAdmin)
			table.update(u, u.changeField(schema.emailValidated, true));
	}

	public void handleChangePassword(WikiventsCall call, User u) {
		String newPassword= call.request.getParameter("newPassword");
		String checkNewPassword= call.request.getParameter("checkNewPassword");
		if (! newPassword.equals(checkNewPassword))
			call.sendError(500, "supplied passwords do not match");
		else {
			changePassword(u,newPassword);
			if (call.user==u)
				call.setUserCookie(u);
		}
	}
	
	@NeedsNoAuthorization
	public void handleAddRecommendation(WikiventsCall call, User user) { 
		if (call.user.mayRecommend(user)) {
			String comment=call.request.getParameter("comment");
			table.update(user, user.addSequenceItem(schema.recommendations, new UserItem(model, call.user, comment)));
		}
	}
	@NeedsNoAuthorization
	public void handleRemoveRecommendation(WikiventsCall call, User user) {
		if (user.isRecommendedBy(call.user))
			table.update(user, user.removeSequenceItem(schema.recommendations, user.findRecommendation(call.user._id)));
	}

	public RemoveRecommendationCommand createRemoveRecommendationCommand(WikiventsCall call, User record) {
		String recommenderId=call.request.getParameter("recommender");
		return new RemoveRecommendationCommand(record, recommenderId);
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
			this.username = new InputField(schema.username, new UniqueKeyIndexValidator<User>(call.model.usernameIndex) );
			this.email    = new InputField(schema.email, this::validateEmail, new UniqueKeyIndexValidator<User>(call.model.emailIndex) );
		}
		public final InputField username;
		public final InputField email;
		public final InputField city     = new InputField(schema.city);
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
	
	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
	public void handleUploadAvatar(WikiventsCall call, User u) {
		if (call.request.getContentType() != null && call.request.getContentType().startsWith("multipart/form-data"))
			call.baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		OutputStream out = null;
		InputStream filecontent = null;
		try {
			final Part filePart = call.request.getPart("avatar");
			final String fileName = StringUtil.urlify(getFileName(filePart)); 
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
			table.update(u, u.changeField(schema.avatarUrl, "/user/:"+u._id+"/uploaded/"+fileName));
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
		call.redirect("/user/"+u.username);
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void handleAddTag(WikiventsCall call, User rec) {
		String tag=call.request.getParameter("tag");
		tag= Tag.normalize(tag);
		CallInfo.instance.get().action="handleAddTag "+tag;
		if (!rec.tags.endsWith(","))
			tag=","+tag;
		if (! rec.hasTag(tag))
			table.update(rec, rec.changeField(schema.tags, rec.tags+tag+","));
	}
	public void handleRemoveTag(WikiventsCall call, User rec) {
		String tag=call.request.getParameter("tag");
		tag= Tag.normalize(tag);
		CallInfo.instance.get().action="handleRemoveTag "+tag;
		if (rec.hasTag(tag))
			table.update(rec, rec.changeField(schema.tags, rec.tags.replaceAll(tag+",","")));
	}

	@NeedsNoAuthentication
	public void viewUnsubscribeMonthlyNewsletter(WikiventsCall call, User user) {
		TemplateData context = call.createTemplateData();
		String message=null;
		String token=call.request.getParameter("token");
		if (user==null)
			message="onbekende gebruiker";
		else if (token==null || ! token.equals(user.secureToken(User.tokenUnsubscribe)))
			message="ongeldige token om maandelijkse nieuwsbrief instelling aan te passen voor "+user.username;
		else {
			table.update(user, user.changeField(schema.subscribeMonthlyMail, false));
			message="Maandelijkse nieuwsbrief voor " + user.email + " is uitgeschakeld";
		}
		context.add("message",message);
		call.output(call.getTheme().simpleMessage, context);
	}
	@NeedsNoAuthentication
	public void viewUnsubscribeWeeklyNewsletter(WikiventsCall call, User user) {
		TemplateData context = call.createTemplateData();
		String message=null;
		String token=call.request.getParameter("token");
		if (user==null)
			message="onbekende gebruiker";
		else if (token==null || ! token.equals(user.secureToken(User.tokenUnsubscribe)))
			message="ongeldige token om wekelijkse nieuwsbrief instelling aan te passen voor "+user.username;
		else {
			table.update(user, user.changeField(schema.subscribeWeeklyActivities, false));
			message="Wekelijkse nieuwsbrief voor " + user.email + " is uitgeschakeld";
		}
		context.add("message",message);
		call.output(call.getTheme().simpleMessage, context);
	}
}

