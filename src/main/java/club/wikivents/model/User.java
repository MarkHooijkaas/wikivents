package club.wikivents.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.http4j.handlebar.Htmlable;
import org.kisst.item4j.Item;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.PasswordEncryption;
import org.kisst.util.PasswordEncryption.HasPasswordSalt;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.collections.MappingChange.Map;


public class User extends CrudObject implements AccessChecker<User>, Htmlable, HasPasswordSalt {
	private final static InternetAddress systemMailAddress;
	static {
		try {
			systemMailAddress=new InternetAddress("info@wikivents.nl","Wikivents beheer");
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e);}
	}

	
	public final WikiventsModel model;
	public final String username;
	public final String description;
	public final String message;
	public final String email;
	public final String city;
	public final String avatarUrl;
	public final String passwordResetToken;
	public final String passwordSalt;
	public final String encryptedPassword;
	public final boolean isAdmin;
	public final boolean emailValidated;
	public final boolean blocked;
	public final boolean identityValidated;

	public User(WikiventsModel model, Struct data) {
		super(model.users, data);
		int dataversion = getCrudObjectVersionOf(data);
		this.model=model;
		this.username=schema.username.getString(data);
		this.description=schema.description.getString(data,null);
		this.message=schema.message.getString(data,null);
		this.email=schema.email.getString(data);
		this.city=schema.city.getString(data);
		this.avatarUrl=schema.avatarUrl.getString(data,null);
		this.passwordResetToken=schema.passwordResetToken.getString(data,null);
		this.passwordSalt=schema.passwordSalt.getString(data);
		this.encryptedPassword=schema.encryptedPassword.getString(data);
		this.isAdmin=schema.isAdmin.getBoolean(data,false);
		this.emailValidated=schema.emailValidated.getBoolean(data,false);
		this.blocked=schema.blocked.getBoolean(data,false);
		boolean defaultValue = dataversion==0;
		this.identityValidated=schema.identityValidated.getBoolean(data,defaultValue);
	}
	@Override public int getCrudObjectVersion() { return 1;}
	@Override public String getPasswordSalt() { return passwordSalt; }

	
	public ArrayList<Event> futureEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.futureEvents()) {
			if (e.hasGuest(this) || e.hasOrganizer(this))
				result.add(e);
		}
		return result;
	}
	public ArrayList<Event> pastEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.pastEvents()) {
			if (e.hasGuest(this) || e.hasOrganizer(this))
				result.add(e);
		}
		return result;
	}

	public ArrayList<Group> groups() {
		ArrayList<Group> result=new ArrayList<Group>();
		for (Group gr: model.groups) {
			if (gr.hasMember(this) || gr.hasOwner(this))
				result.add(gr);
		}
		return result;
	}

	
	@Override public String getHtmlString() { return link(); }
	public String link() {
		String img=avatarUrl;
		
		if (img==null || img.trim().length()==0)
			img="/favicon.ico";
		
		img="<img class=\"link-avatar\" src=\""+img+"\"> ";
		return "<a href=\"/user/"+username+"\" data-toggle=\"tooltip\" title=\""+username+"\">"+img+username+"</a>"; 
	} 
	public String usernameLink() { return "<a href=\"/user/"+username+"\">"+username+"</a>";	} 
	public String avatarLink() {
		String img=avatarUrl;
		if (img==null || img.trim().length()==0)
			img="/favicon.ico";
		img="<img class=\"link-avatar\" src=\""+img+"\"> ";
		return "<a href=\"/user/"+username+"\" data-toggle=\"tooltip\" title=\""+username+"\">"+img+"</a>"; 
	} 

	
	
	public static class Ref extends CrudRef<User> implements CrudModelObject, Htmlable {
		public static final Type<User.Ref> type = new Type.Java<User.Ref>(User.Ref.class, null); // XXX TODO: parser is null 
		public static class Field extends Schema.BasicField<User.Ref> {
			public Field(String name) { super(User.Ref.type, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return new User.Ref(model, Item.asString(data.getDirectFieldValue(name)));}
		}

		public Ref(WikiventsModel model, String _id) { super(model.users, _id); }
		@Override public String getHtmlString() { return link(); }
		public String link() { 
			User u=get0();
			if (u==null)
				return "unknown";
			return u.link();
		} 
		public String name() {
			 // TODO: this will be inefficient if database not in memory
			User u=get0();
			if (u==null)
				return "unknown";
			return u.username; 
		}
	}
	
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudSchema<User> {
		private Schema() { super(User.class); }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField username = new StringField("username"); 
		public final StringField description= new StringField("description"); 
		public final StringField message = new StringField("message"); 
		public final StringField email    = new StringField("email"); 
		public final StringField city = new StringField("city"); 
		public final StringField avatarUrl= new StringField("avatarUrl"); 
		public final StringField passwordResetToken = new StringField("passwordResetToken"); 
		public final StringField passwordSalt = new StringField("passwordSalt"); 
		public final StringField encryptedPassword = new StringField("encryptedPassword"); 
		public final BooleanField isAdmin = new BooleanField("isAdmin");
		public final BooleanField emailValidated = new BooleanField("emailValidated");
		public final BooleanField blocked = new BooleanField("blocked");
		public final BooleanField identityValidated = new BooleanField("identityValidated");
	}
	
	public boolean mayBeViewedBy(User user) {
		if (user==null) return false;
		if (_id.equals(user._id))	return true;
		if (user.trusted()) return true;
		return false;		
	}
	public boolean mayBeChangedBy(User u) {
		if (u==null) return false;
		if (_id.equals(u._id))
			return true;
		return u.isAdmin;
	}
	
	public boolean trusted() { return identityValidated && emailValidated && ! blocked; }
	public int karma() {
		if (blocked)
			return -100;
		if (! emailValidated)
			return 0; // TODO: - complaints.size
		int karma=0;
		// karma += recommendations.size()
		// karma -= complaints.size()
		if (identityValidated)
			karma+=10;
		return karma;
	}
	public boolean karmaPositive() { return karma()>0; }
	public boolean karmaNeutral() { return karma()==0; }
	public boolean karmaNegative() { return karma()<0; }
	public boolean karmaNotNegative() { return karma()>=0; }
	public boolean karmaNotPositive() { return karma()<=0; }
	
	public boolean canReceiveMail() { return emailValidated && karma()>0; }
	public boolean maySeeSender() { return karma()>0; }
	public boolean maySendMail() { return karma()>0; }
	public boolean mayComment() { return karma()>0; }
	public boolean mayParticipate() { return karma()>0; }
	public boolean maySeeProfile() { return karma()>0; }
	public boolean maySeePicture() { return karma()>0; }
	public boolean mayRecommend() { return identityValidated && karma()>=13; }
	
	public String getNotifications() throws IOException { 
		final InputStream in = new FileInputStream("json.json");
		try {
		  for (Iterator it = new ObjectMapper().readValues(
		      new JsonFactory().createJsonParser(in), Map.class); it.hasNext();)
		    System.out.println(it.next());
		}
		finally { in.close();} 
		return readBlob("messages.dat"); 
	}
	public void addNotification(String message) { appendBlob("messages.dat", message); }

	
	public String karmaIcon() { 
		int k=karma();
		if (k>0) return "/images/yin_yang_green.gif";
		if (k==0) return "/images/yin_yang_grey.png";
		return "/images/yin_yang_red.png";
	}
	
	public void sendSystemMail(String subject, String message) {
		sendMailFrom(systemMailAddress, subject, message, true);
	}
	
	public void sendMailFrom(User from, String subject, String message, boolean copyToSender) {
		if (! from.trusted())
			throw new RuntimeException("User "+from.username+" not trusted to send email to "+this.username+" about "+subject);
		try {
			sendMailFrom(new InternetAddress(from.email,from.username), subject, message, copyToSender);
		} 
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
	}
	public void sendMailFrom(InternetAddress from, String subject, String message, boolean copyToSender) {
		try {
			final MimeMessage msg = model.site.emailer.createMessage();
			if (from==systemMailAddress)
				msg.setFrom(from);
			else {
				msg.setFrom(new InternetAddress("info@wikivents.nl","Wikivents gebruiker "+from.getPersonal()));
				if (this.trusted())
					msg.setReplyTo(new InternetAddress[] {from});
			}
			msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] {new InternetAddress(email, username)});
			if (copyToSender)
				msg.setRecipients(Message.RecipientType.CC, new InternetAddress[] {from});
			msg.setSubject(subject);
			if (message.trim().startsWith("<"))
				msg.setContent(message, "text/html; charset=utf-8");
			else
				msg.setText(message, "utf-8");
			model.site.emailer.send(msg);
		} 
		catch (MessagingException e) { throw new RuntimeException(e); } 
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
	}
	
	
	public void changePassword(String newPassword) {
		String salt = PasswordEncryption.generateSalt();
		String pw = PasswordEncryption.encryptPassword(newPassword, salt);
		model.users.updateFields(this, new HashStruct()
			.add(schema.passwordSalt,  salt)
			.add(schema.encryptedPassword, pw)
		);
	}
	
	
	public boolean checkPassword(String password) {
		if (password==null || encryptedPassword==null || passwordSalt==null)
			return false;
		if (PasswordEncryption.authenticate(password, encryptedPassword, passwordSalt))
			return true;
		try {
			Thread.sleep(1000); // prevent brute force
		}
		catch (InterruptedException e) {throw new RuntimeException(e); }
		return false;
	}
}
