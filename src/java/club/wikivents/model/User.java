package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.struct4j.Struct;

public class User extends CrudObject {
	public final String name;
	public final String email;
	public final String password;
	
	public User(Struct s) {
		super(schema, s);
		this.name=schema.name.getString(s);
		this.email=schema.email.getString(s);
		this.password=schema.password.getString(s);
		
	}

	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<User> {
		public Schema() { super(User.class); addAllFields();}
		public final StringField name     = new StringField(User.class, "name"); 
		public final StringField email    = new StringField(User.class, "email"); 
		public final StringField password = new StringField(User.class, "password"); 
	}
}
