package club.wikivents.model;

import java.util.ArrayList;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.PasswordEncryption;


public class User extends CrudObject implements AccessChecker<User>{
	public final WikiventsModel model;
	public final String username;
	public final String email;
	public final String city;
	public final String avatarUrl;
	public final String passwordSalt;
	public final String encryptedPassword;
	public final boolean isAdmin;
	public final Immutable.Sequence<Friend> friends;

	public User(WikiventsModel model, Struct data) {
		super(model.users, data);
		this.model=model;
		this.username=schema.username.getString(data);
		this.email=schema.email.getString(data);
		this.city=schema.city.getString(data);
		this.avatarUrl=schema.avatarUrl.getString(data);
		this.passwordSalt=schema.passwordSalt.getString(data);
		this.encryptedPassword=schema.encryptedPassword.getString(data);
		this.friends=schema.friends.getSequence(model, data);
		this.isAdmin=schema.isAdmin.getBoolean(data,false);
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
	
	
	public String link() {
		String img="";
		if (avatarUrl!=null)
			img="<img class=\"link-avatar\" src=\""+avatarUrl+"\"> ";
		return "<a href=\"/user/show/"+username+"\">"+img+username+"</a>"; 
	} 

	
	
	public static class Ref extends CrudRef<User> implements CrudModelObject {
		public static final Type<User.Ref> type = new Type.Java<User.Ref>(User.Ref.class, null); // XXX TODO: parser is null 
		public static class Field extends Schema.BasicField<User.Ref> {
			public Field(String name) { super(User.Ref.type, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return new User.Ref(model, data.getString(name));}
		}

		public Ref(WikiventsModel model, String _id) { super(model.users, _id); }
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
		public final StringField username = new StringField("username"); 
		public final StringField email    = new StringField("email"); 
		public final StringField city = new StringField("city"); 
		public final StringField avatarUrl= new StringField("avatarUrl"); 
		public final StringField passwordSalt = new StringField("passwordSalt"); 
		public final StringField encryptedPassword = new StringField("encryptedPassword"); 
		public final BooleanField isAdmin = new BooleanField("isAdmin"); 
		public final SequenceField<Friend> friends  = new SequenceField<Friend>(Friend.schema,"friends");  
	}
	
	public boolean mayBeViewedBy(User user) {
		if (user==null) return false;
		if (_id.equals(user._id))	return true;
		if (user.isAdmin) return true;
		return hasFriend(user);		
	}
	public boolean mayBeChangedBy(User u) {
		if (u==null) return false;
		if (_id.equals(u._id))
			return true;
		return u.isAdmin;
	}
	public boolean isFriendOf(User u) { return u.hasFriend(this); }
	public boolean hasFriend(User user) {
		for (Friend f: friends) 
			if (f.user._id.equals(user._id)) 
				return true;
		return false;
	}
	
	public void addFriend(WikiventsModel model, User user) {
		for (Friend f: friends) // check if already in my friends
			if (f.user._id.equals(user._id)) // TODO: when Friend with no user, this will throw NPE
				return;
		model.users.addSequenceItem(this, schema.friends, new Friend(model, user));
	}
	
	public void changePassword(String newPassword) {
		String salt = PasswordEncryption.createSaltString();
		String pw = PasswordEncryption.encryptPassword(newPassword, salt);
		model.users.updateFields(this, new HashStruct()
			.add(schema.passwordSalt,  salt)
			.add(schema.encryptedPassword, pw)
		);
	}
	
	
	public boolean checkPassword(String password) {
		if (password==null)
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
