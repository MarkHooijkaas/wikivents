package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.index.UniqueIndex;
import org.kisst.item4j.struct.Struct;

public class User extends CrudObject {
	public final String username;
	public final String email;
	public final String password;
	
	public User(Struct s) {
		super(schema, s);
		this.username=schema.username.getString(s);
		this.email=schema.email.getString(s);
		this.password=schema.password.getString(s);
		
	}
	@Override public boolean mayBeChangedBy(String userId) { return userId!=null && userId.equals(_id); }

	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<User> {
		public Schema() { super(User.class); addAllFields();}
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField username = new StringField("username"); 
		public final StringField email    = new StringField("email"); 
		public final StringField password = new StringField("password"); 
	}
	
	public static class Table extends CrudTable<User> {
		public final UniqueIndex usernameIndex;
		public final UniqueIndex emailIndex;
		
		public class Ref extends CrudTable<User>.Ref { public Ref(String id) { super(id); } }
		@Override public Ref createRef(String _id) { return new Ref(_id); }
		
		public Table(WikiventsModel model) { 
			super(User.schema, model, model.getStorage(User.class));
			this.usernameIndex=model.getUniqueIndex(User.class, User.schema.username);
			this.emailIndex=model.getUniqueIndex(User.class, User.schema.email);
		}
		@Override public User createObject(Struct props) { return new User(props); }
		
		public User findUsername(String username) {
			for (User u: this) { // TODO: use index on username and on email
				if (u.username.equals(username) || u.email.equals(username))
					return u;
			}
			return null;
		}
	}
}
