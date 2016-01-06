package club.wikivents.model;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
//import java.util.Iterator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kisst.http4j.SecureToken;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.http4j.handlebar.Htmlable;
import org.kisst.item4j.Item;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel.MyObject;
import org.kisst.pko4j.PkoTable.KeyRef;
import org.kisst.util.PasswordEncryption;
import org.kisst.util.PasswordEncryption.HasPasswordSalt;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.javafx.collections.MappingChange.Map;


public class User extends UserData implements AccessChecker<User>, Htmlable, HasPasswordSalt {
	public User(WikiventsModel model, Struct data) { super(model, data); }

	public final static InternetAddress systemMailAddress;
	static {
		try {
			systemMailAddress=new InternetAddress("info@wikivents.nl","Wikivents beheer");
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e);}
	}


	public static int getCurrentPkoVersion() { return 1;}

	@Override public int getPkoVersion() { return getCurrentPkoVersion();}
	@Override public String getPasswordSalt() { return passwordSalt; }

	public String getLoginToken() { 
		SecureToken tok=new SecureToken(model, _id);
		return tok.getToken();
	}
	
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

	
	@Override protected Ref createRef() { return new Ref(model, _id); }
	@Override public Ref getRef() { return (Ref) super.getRef(); }
	public static class Ref extends KeyRef<WikiventsModel,User> implements MyObject, Htmlable {
		public static final Type<User.Ref> type = new Type.Java<User.Ref>(User.Ref.class, null); // XXX TODO: parser is null 
		public static class Field extends Schema.BasicField<User.Ref> {
			public Field(String name) { super(User.Ref.type, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return (Ref) model.users.createRef(Item.asString(data.getDirectFieldValue(name)));}
		}

		private Ref(WikiventsModel model, String _id) { super(model.users, _id); }
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
	
	@Override public boolean mayBeViewedBy(User user) {
		if (user==null) return false;
		if (_id.equals(user._id))	return true;
		if (user.trusted()) return true;
		return false;		
	}
	@Override public boolean mayBeChangedBy(User u) {
		if (u==null) return false;
		if (_id.equals(u._id))
			return true;
		return u.isAdmin;
	}
	@Override public boolean fieldMayBeChangedBy(String field, User user) { 
		if (! mayBeChangedBy(user))
			return false;
		if (user.isAdmin)
			return true;
		if (schema.isAdmin.name.equals(field))
			return false;
		//if (schema.identityValidated.name.equals(field))
		//	return false;
		//if (schema.emailValidated.name.equals(field))
		//	return false;
		return true;
	}

	public boolean trusted() { return (karma()>=10) && emailValidated && ! blocked; }
	public int karma() {
		if (blocked)
			return -100;
		if (! emailValidated)
			return 0;
		int karma=10*recommendations.size();
		if (isAdmin)
			karma+=100;
		// karma -= complaints.size()
		return karma;
	}
	public boolean karmaPositive() { return karma()>0; }
	public boolean karmaNeutral() { return karma()==0; }
	public boolean karmaNegative() { return karma()<0; }
	public boolean karmaNotNegative() { return karma()>=0; }
	public boolean karmaNotPositive() { return karma()<=0; }
	
	public boolean canReceiveMail() { return emailValidated && karma()>0; }
	public boolean maySeeUsers() { return karma()>0; }
	public boolean maySeeSender() { return karma()>0; }
	public boolean maySendMail() { return karma()>0; }
	public boolean mayComment() { return karma()>0; }
	public boolean mayParticipate() { return karma()>0; }
	public boolean maySeeProfile() { return karma()>0; }
	public boolean maySeePicture() { return karma()>0; }
	public boolean mayRecommend() { return karma()>=20; }
	public boolean mayRecommend(User other) { 
		if (other==null) return false;
		if (! mayRecommend()) return false;
		if (other.isRecommendedBy(this)) return false;
		return ! this._id.equals(other._id);
	}

	
	/*
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
*/
	
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
	
	public boolean isRecommendedBy(User user) { return recommendations.hasItem(UserItem.key,user._id); }
	public UserItem findRecommendation(String id) { return recommendations.findItemOrNull(UserItem.key, id); }
}
