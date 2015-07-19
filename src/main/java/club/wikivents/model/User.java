package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
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
	@Override public boolean mayBeChangedBy(String userId) { return userId.endsWith(_id); }

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
		public class Ref extends CrudTable<User>.Ref { public Ref(String id) { super(id); } }
		@Override public Ref createRef(String _id) { return new Ref(_id); }
		
		public Table(StructStorage storage, WikiventsModel model) { super(User.schema, model, storage);}
		@Override public User createObject(Struct props) { return new User(props); }
	}
}
