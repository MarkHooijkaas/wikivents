package club.wikivents.model;

import org.kisst.http4j.handlebar.Htmlable;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel.MyObject;
import org.kisst.pko4j.PkoRef;
import org.kisst.pko4j.PkoSchema;


public abstract class UserData extends WikiventsObject<User> {
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	public static final Schema schema=new Schema();
	public static final class Schema extends PkoSchema<User> {
		private Schema() { super(User.class); }
		@Override public int getCurrentVersion() { return 1; }
		public final IdField _id = new IdField();
		public final StringField username = new StringField("username"); 
		public final StringField description= new StringField("description"); 
		public final StringField email    = new StringField("email"); 
		public final StringField city = new StringField("city"); 
		public final StringField avatarUrl= new StringField("avatarUrl"); 
		public final StringField passwordResetToken = new StringField("passwordResetToken"); 
		public final StringField passwordSalt = new StringField("passwordSalt"); 
		public final StringField encryptedPassword = new StringField("encryptedPassword"); 
		public final BooleanField isAdmin = new BooleanField("isAdmin");
		public final BooleanField emailValidated = new BooleanField("emailValidated");
		public final BooleanField blocked = new BooleanField("blocked");
		public final SequenceField<UserItem> recommendations= new SequenceField<>(UserItem.class,"recommendations");
	}
	
	public final String username;
	public final String description;
	public final String email;
	public final String city;
	public final String avatarUrl;
	public final String passwordResetToken;
	public final String passwordSalt;
	public final String encryptedPassword;
	public final boolean isAdmin;
	public final boolean emailValidated;
	public final boolean blocked;
	public final ImmutableSequence<UserItem> recommendations;

	protected UserData(WikiventsModel model, Struct data, int version) {
		super(model, model.users, data);
		this.username=schema.username.getString(data);
		this.description=schema.description.getString(data,null);
		this.email=schema.email.getString(data);
		this.city=schema.city.getString(data);
		this.avatarUrl=schema.avatarUrl.getString(data,null);
		this.passwordResetToken=schema.passwordResetToken.getString(data,null);
		this.passwordSalt=schema.passwordSalt.getString(data);
		this.encryptedPassword=schema.encryptedPassword.getString(data);
		this.isAdmin=schema.isAdmin.getBoolean(data,false);
		this.emailValidated=schema.emailValidated.getBoolean(data,false);
		this.blocked=schema.blocked.getBoolean(data,false);
		this.recommendations=startingRecommendation(data, version);
	}
	private ImmutableSequence<UserItem> startingRecommendation(Struct data, int version) {
		boolean defaultValue = version==0;
		boolean identityValidated =  Item.asBoolean(data.getDirectFieldValue("identityValidated",defaultValue));
		if (! identityValidated)
			return schema.recommendations.getSequenceOrEmpty(table.model, data);
		MultiStruct data2=new MultiStruct(
			new SingleItemStruct(UserItem.schema.date.name, ""+this.creationDate),
			new SingleItemStruct(UserItem.schema.user.name, "55bd0486a1e0df4a250cd3eb")
		);
		UserItem item=new UserItem(model, data2);
		return ImmutableSequence.of(UserItem.class, item);
	}	
	
	@Override public Ref getRef() { return Ref.of(model,_id); }
	public static class Ref extends PkoRef<User> implements MyObject, Htmlable {
		// TODO: think of structural solution for refs with a null key
		static public Ref of(WikiventsModel model, String key) { return key==null ? null : new Ref(model, key); }
		public static class Field extends Schema.BasicField<User.Ref> {
			public Field(String name) { super(User.Ref.class, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return of(model,Item.asString(data.getDirectFieldValue(name)));}
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
			User u=get0();
			if (u==null)
				return "unknown";
			return u.username; 
		}
	}
	@Override public String getName() { return username; }
}
