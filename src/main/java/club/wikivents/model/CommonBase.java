package club.wikivents.model;

import java.util.StringJoiner;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructProps;
import org.kisst.pko4j.PkoModel.MyObject;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoSchema;
import org.kisst.pko4j.PkoTable;
import org.kisst.props4j.Props;
import org.kisst.util.StringUtil;

public abstract class CommonBase<T extends CommonBase<T>> extends WikiventsObject<T> implements Item.Factory {
	public static Schema<Wikivent> commonSchema=new Schema<>(Wikivent.class); //TODO: have a schema with a neutral class
	public static class Schema<T extends PkoObject> extends PkoSchema<T> {
		protected Schema(Class<T> cls) { super(cls); }
		public final IdField _id = new IdField();
		public final InstantField creationDate = new InstantField("creationDate");
		public final InstantField modificationDate = new InstantField("modificationDate");
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField urlName= new StringField("urlName"); 
		public final BooleanField invitedOnly = new BooleanField("invitedOnly");
		public final BooleanField hidden = new BooleanField("hidden");
		public final SequenceField<User.Ref> owners = new SequenceField<User.Ref>(User.Ref.class,"owners");
		public final SequenceField<User.Ref> members= new SequenceField<>(User.Ref.class,"members"); 
		public final SequenceField<User.Ref> invitedUsers= new SequenceField<>(User.Ref.class,"invitedUsers"); 
		public final SequenceField<User.Ref> likes = new SequenceField<User.Ref>(User.Ref.class,"likes");
		public final StringField description = new StringField("description"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.class,"comments");
		public final SequenceField<Poll> polls = new SequenceField<>(Poll.class,"polls");
	}
	
	public final String title;
	public final String urlName;
	public final String description;
	public final boolean invitedOnly;
	public final boolean hidden;
	public final ImmutableSequence<User.Ref> owners;
	public final ImmutableSequence<User.Ref> members;
	public final ImmutableSequence<User.Ref> invitedUsers;
	public final ImmutableSequence<User.Ref> likes;
	public final ImmutableSequence<Comment> comments;
	public final ImmutableSequence<Poll> polls;
	
	public static class OldEventSchema<T extends PkoObject> extends PkoSchema<T> {
		private OldEventSchema(Class<T> cls) { super(cls); }
		private final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.class,"guests");
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(User.Ref.class,"organizers");
	}
	private static OldEventSchema<Wikivent> oldEventSchema = new OldEventSchema<Wikivent>(Wikivent.class);
	
	public CommonBase(Schema<T> schema, WikiventsModel model, PkoTable<T> table, Struct data) {
		super(model, table, data);
		this.title=schema.title.getString(data);
		String urlName=schema.urlName.getString(data);
		if (urlName==null)
			this.urlName=StringUtil.urlify(title).toLowerCase();
		else
			this.urlName=urlName;
		this.description=schema.description.getString(data);
		this.invitedOnly=schema.invitedOnly.getBoolean(data, false);
		this.hidden=schema.hidden.getBoolean(data, false);
		
			if (schema.owners.fieldExists(data))
			this.owners=schema.owners.getSequenceOrEmpty(model, data);
		else
			this.owners=oldEventSchema.organizers.getSequenceOrEmpty(model, data);

		if (schema.members.fieldExists(data))
			this.members=schema.members.getSequenceOrEmpty(model, data);
		else {
			ImmutableSequence<Guest> guests = oldEventSchema.guests.getSequenceOrEmpty(model, data);
			User.Ref[] arr=new User.Ref[guests.size()];
			int i=0;
			for (Guest g: guests)
				arr[i++]=g.user;
			this.members=ImmutableSequence.of(User.Ref.class, arr);
		}
		this.invitedUsers=schema.invitedUsers.getSequenceOrEmpty(model, data);

		this.likes=schema.likes.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
		this.polls=schema.polls.getSequenceOrEmpty(this, data);
	}
	

	@Override public String getName() { return title; }

	public static class Poll extends ReflectStruct implements MyObject {
		public final String title;
		public final String description;
		public final ImmutableSequence<Option> options;
		public final  WikiventsModel model;
		public Poll(WikiventsModel model, Struct data) {
			Props props=StructProps.of(data);
			this.model=model;
			this.title=props.getString("title");
			this.description=props.getString("description");
			this.options=props.getTypedSequenceOrEmpty(model, Option.class, "options");
		}
		
		public class Option {
			public final String title;
			public final String description;
			public final ImmutableSequence<Answer> answers;
			public Option(Struct data) {
				Props props=StructProps.of(data);
				this.title=props.getString("title");
				this.description=props.getString("description");
				this.answers=props.getTypedSequenceOrEmpty(model, Answer.class, "answers");
			}

			public class Answer {
				public final User.Ref user;
				public final PollAnswer possible;
				public final String comment;
				public Answer(Struct data) {
					Props props=StructProps.of(data);
					this.comment=props.getString("title",null);
					this.user=null;
					this.possible=PollAnswer.valueOf(props.getString("possible"));
				}
			}
		}
	}
	enum PollAnswer { YES, NO, MAYBE }

	@Override public Object construct(Class<?> cls, Object data) {
		if (Poll.class==cls && data instanceof Struct)
			return new Poll(model, (Struct)data);
		return model.construct(cls, data);
	};
	
	public String ownerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (User.Ref r : owners)
			sj.add(r.get().getName());
		return sj.toString();
	}

	public boolean isVisible() { return !hidden; }

	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOwner(user)); }
	@Override public boolean mayBeViewedBy(User user) { 
		return (!hidden) || hasOwner(user) || hasMember(user) || hasInvitedUser(user);
	}
	public boolean mayBeJoinedBy(User user) { return (! invitedOnly) || hasOwner(user) || hasInvitedUser(user); }
	public boolean needsInviteMechanism() { return hidden || invitedOnly; }

	public boolean hasOwner(User.Ref user) { return owners.contains(user); }
	public boolean hasMember(User.Ref user) { return members.contains(user); }
	public boolean hasInvitedUser(User.Ref user) { return invitedUsers.contains(user); }

	public final boolean hasOwner(User user) { return user!=null && hasOwner(user.getRef()); }
	public final boolean hasMember(User user) { return user!=null && hasMember(user.getRef()); }
	public final boolean hasInvitedUser(User user) { return user!=null && hasInvitedUser(user.getRef()); }

	private static ImmutableSequence.StringExpression userRefKey=(ref) -> {return ((User.Ref) ref).getKey(); };
	public boolean isLikedBy(User user) { return likes.hasItem(userRefKey, user._id); }


	public Comment findComment(String id) { 
		if (comments==null || id==null)
			return null;
		for (Comment com : comments)
			if (id.equals(com.id()))
				return com;
		return null;
	}

}
