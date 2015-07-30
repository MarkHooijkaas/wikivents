package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudObjectSchema;
import org.kisst.item4j.struct.Struct;

public class User extends CrudObject {
	public final String username;
	public final String email;
	public final String password;
	
	public User(WikiventsModel model, Struct data) {
		super(model.users, data);
		this.username=schema.username.getString(data);
		this.email=schema.email.getString(data);
		this.password=schema.password.getString(data);
		
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudObjectSchema<User> {
		public Schema() { super(User.class); addAllFields();}
		public final StringField username = new StringField("username"); 
		public final StringField email    = new StringField("email"); 
		public final StringField password = new StringField("password"); 
	}
	@Override public boolean mayBeChangedBy(String userId) { return _id.equals(userId); }
}
