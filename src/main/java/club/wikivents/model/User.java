package club.wikivents.model;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudObjectSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.struct.Struct;

public class User extends CrudObject implements AccessChecker<User>{
	public final WikiventsModel model;
	public final String username;
	public final String email;
	public final String password;
	public final boolean isAdmin;
	public final Immutable.Sequence<Friend> friends;

	public User(WikiventsModel model, Struct data) {
		super(model.users, data);
		this.model=model;
		this.username=schema.username.getString(data);
		this.email=schema.email.getString(data);
		this.password=schema.password.getString(data);
		this.friends=schema.friends.getSequence(model, data);
		this.isAdmin=schema.isAdmin.getBoolean(data,false);
	}
	
	public static class Ref extends CrudRef<User> implements CrudModelObject {
		public Ref(WikiventsModel model, String _id) { super(model.users, _id); }
		public String name() { return get().username; } // TODO: this will be inefficient if database not in memory
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudObjectSchema<User> {
		public Schema() { super(User.class); addAllFields();}
		public final StringField username = new StringField("username"); 
		public final StringField email    = new StringField("email"); 
		public final StringField password = new StringField("password"); 
		public final BooleanField isAdmin = new BooleanField("isAdmin"); 
		public final SequenceField<Friend> friends  = new SequenceField<Friend>(Friend.class,"friends");  
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
		Friend newFriend = new Friend(model, user);
		User newUser= this.modified(model, schema.friends, friends.growTail(newFriend));
		model.users.update(this, newUser);
	}
}
