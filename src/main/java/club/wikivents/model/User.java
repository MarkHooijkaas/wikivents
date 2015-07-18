package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Type;
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

	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<User> {
		public Schema() { super(User.class); addAllFields();}
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField username = new StringField("username"); 
		public final StringField email    = new StringField("email"); 
		public final StringField password = new StringField("password"); 
		public class RefField extends CrudSchema<User>.Field {
			public RefField(String name) { super(Type.javaString,name); }
			@Override public Type.JavaString getType() { return Type.javaString; }
			public CrudTable<User>.Ref getRef(CrudTable<User> table, Struct s) { 
				return table.createRef(s.getString(getName()));
			}
		}
	}
	
	public static class Table extends CrudTable<User> {
		public class Ref extends CrudTable<User>.Ref { public Ref(String id) { super(id); } }
		@Override public Ref createRef(String _id) { return new Ref(_id); }
		
		public Table(StructStorage storage, WikiventsModel model) { super(User.schema, model, storage);}
		@Override public User createObject(Struct props) { return new User(props); }
	}
}
