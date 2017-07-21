package club.wikivents.model;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kisst.http4j.SecureToken;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.http4j.handlebar.Htmlable;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.PasswordEncryption;
import org.kisst.util.PasswordEncryption.HasPasswordSalt;

import com.github.jknack.handlebars.Handlebars;

//import java.util.Iterator;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.javafx.collections.MappingChange.Map;


public class User extends UserData implements AccessChecker<User>, Htmlable, HasPasswordSalt {
	public User(WikiventsModel model, Struct data, int version) { super(model, data, version); }
	public User(WikiventsModel model, Struct data) { super(model, data, schema.getCurrentVersion()); }

	public final static InternetAddress systemMailAddress;
	static {
		try {
			systemMailAddress=new InternetAddress("info@wikivents.nl","Wikivents beheer");
		}
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e);}
	}

	@Override public String toString() { return "User("+username+")"; }
	@Override public String getPasswordSalt() { return passwordSalt; }
	public String getUrl() {
		if (archived)
			return "/user/:"+this._id;
		return "/user/"+username;}

	public String getLoginToken() { 
		SecureToken tok=new SecureToken(model, _id);
		return tok.getToken();
	}
	
	public ArrayList<Event> futureEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.futureEvents()) {
			if (e.hasMember(this) || e.hasOwner(this))
				result.add(e);
		}
		return result;
	}
	public ArrayList<Event> pastEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.pastEvents()) {
			if (e.hasMember(this) || e.hasOwner(this))
				result.add(e);
		}
		return result;
	}
	public ArrayList<Event> allEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.allEvents) {
			if (e.hasMember(this) || e.hasOwner(this))
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

		if (archived)
			return "<img class=\"link-avatar\" src=\"/white-smiley.png\">"+getName();
		else if (img==null || img.trim().length()==0)
			img="/favicon.ico";
		
		img="<img class=\"link-avatar\" src=\""+img+"\"> ";
		return "<a href=\""+getUrl()+"\" data-toggle=\"tooltip\" title=\""+getName()+"\">"+img+getName()+"</a>";
	} 
	public String usernameLink() { return "<a href=\""+getUrl()+"\">"+getName()+"</a>";	}
	public String avatarLink() {
		String img=avatarUrl;
		if (archived)
			img="/white-smiley.png";
		else if (img==null || img.trim().length()==0)
			img="/favicon.ico";
		img="<img class=\"link-avatar\" src=\""+img+"\"> ";
		return "<a href=\""+getUrl()+"\" data-toggle=\"tooltip\" title=\""+getName()+"\">"+img+"</a>";
	}


	public boolean isOwner(CommonBase<?> obj) { return obj.hasOwner(this); }
	public boolean isMember(CommonBase<?> obj) { return obj.hasMember(this); }
	public boolean mayJoin(CommonBase<?> obj) { return obj.mayBeJoinedBy(this); }
	@Override public boolean mayBeViewedBy(User user) {
		if (user==null) return false;
		if (archived && ! user.isAdmin) return false;
		if (_id.equals(user._id) && ! archived)	return true;
		if (user.maySeeProfile()) return true;
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

	public int karma() {
		if (blocked)
			return -100;
		if (! emailValidated)
			return -1;
		int karma=recommendations.size();
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
	
	public boolean canReceiveMail() { return emailValidated && karma()>=0; }
	public boolean maySeeUsers() { return karma()>=0; }
	public boolean maySeeSender() { return karma()>0; }
	public boolean maySendMail() { return karma()>0; }
	public boolean mayComment() { return karma()>0; }
	public boolean mayParticipate() { return karma()>=0; }
	public boolean mayOrganize() { return karma()>0; }
	public boolean maySeeProfile() { return karma()>=0; }
	public boolean maySeePicture() { return karma()>0; }
	public boolean mayRecommend() { return karma()>=2; }
	public boolean mayRecommend(User other) { 
		if (other==null) return false;
		if (! mayRecommend()) return false;
		if (other.isRecommendedBy(this)) return false;
		return ! this._id.equals(other._id);
	}

	public String[] tagNames() { return tags.toLowerCase().split(",");}
	public Tag[] tagList() { return model.tags.tagList(tags);}
	public Handlebars.SafeString tagLinks() { return model.tags.tagLinks(tags);}
	
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
		if (k==0) return "/images/yin-yang-lime.png";
		if (k==-1) return "/images/yin_yang_grey.png";
		return "/images/yin_yang_red.png";
	}
	
	public void sendSystemMail(String subject, String message) {
		sendMailFrom(systemMailAddress, subject, message, true);
	}
	
	public void sendMailFrom(User from, String subject, String message, boolean copyToSender) {
		if (! from.maySendMail())
			throw new RuntimeException("User "+from.username+" not allowed to send email to "+this.username+" about "+subject);
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
				msg.setFrom(new InternetAddress("info@wikivents.nl","Wikivents: namens gebruiker "+from.getPersonal()));
				if (this.maySendMail())
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
	public boolean hasTag(String tag) { return tags.indexOf(","+tag+",")>=0; }

	public String secureToken(String info) { return PasswordEncryption.onewayEncrypt(_id+info,passwordSalt,17);}

	public static final String tokenUnsubscribe="Unsubscribe";
	public String unsubscribeToken() { return secureToken(tokenUnsubscribe); }
	public String unsubscribeMonthlyNewsLetterUrl() {
		return "https://wikivents.nl"+getUrl()+"?view=UnsubscribeMonthlyNewsletter&token="+secureToken(tokenUnsubscribe);
	}
	public String unsubscribeWeeklyNewsLetterUrl() {
		return "https://wikivents.nl"+getUrl()+"?view=UnsubscribeWeeklyNewsletter&token="+secureToken(tokenUnsubscribe);
	}
}
