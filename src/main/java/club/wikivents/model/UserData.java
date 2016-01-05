package club.wikivents.model;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoSchema;


public class UserData extends PkoObject<WikiventsModel> {
	public static final Schema schema=new Schema();
	public static final class Schema extends PkoSchema<WikiventsModel, User> {
		private Schema() { super(User.class); }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
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
		//public final BooleanField identityValidated = new BooleanField("identityValidated");
		public final SequenceField<UserItem> recommendations= new SequenceField<UserItem>(UserItem.schema,"recommendations");
	}
	
	public final WikiventsModel model;
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

	public UserData(WikiventsModel model, Struct data) {
		super(model, model.users, data);
		this.model=model;
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
		this.recommendations=startingRecommendation(data);
	}
	private ImmutableSequence<UserItem> startingRecommendation(Struct data) {
		int dataversion = getPkoVersionOf(data);
		boolean defaultValue = dataversion==0;
		boolean identityValidated =  Item.asBoolean(data.getDirectFieldValue("identityValidated",defaultValue));
		if (! identityValidated)
			return schema.recommendations.getSequenceOrEmpty(model, data);
		MultiStruct data2=new MultiStruct(
			new SingleItemStruct(UserItem.schema.date.name, ""+this.creationDate),
			new SingleItemStruct(UserItem.schema.user.name, "55bd0486a1e0df4a250cd3eb")
		);
		UserItem item=new UserItem(model, data2);
		return ImmutableSequence.of(UserItem.class, item);
	}	
}
