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

public abstract class CommonBase<T extends CommonBase<T>> extends WikiventsObject<T> implements Item.Factory{
	public static class Schema<T extends PkoObject> extends PkoSchema<T> {
		protected Schema(Class<T> cls) { super(cls); }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField urlName= new StringField("urlName"); 
		public final SequenceField<User.Ref> owners = new SequenceField<User.Ref>(User.Ref.class,"owners");
		public final SequenceField<User.Ref> members= new SequenceField<>(User.Ref.class,"members"); 
		public final SequenceField<User.Ref> likes = new SequenceField<User.Ref>(User.Ref.class,"likes");
		public final StringField description = new StringField("description"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.class,"comments");
		public final SequenceField<Poll> polls = new SequenceField<>(Poll.class,"polls");
	}
	
	public final String title;
	public final String urlName;
	public final String description;
	public final ImmutableSequence<User.Ref> owners;
	public final ImmutableSequence<User.Ref> members;
	public final ImmutableSequence<User.Ref> likes;
	public final ImmutableSequence<Comment> comments;
	public final ImmutableSequence<Poll> polls;
	
	public CommonBase(Schema<T> schema, WikiventsModel model, PkoTable<T> table, Struct data) {
		super(model, table, data);
		this.title=schema.title.getString(data);
		String urlName=schema.urlName.getString(data);
		if (urlName==null)
			this.urlName=StringUtil.urlify(title).toLowerCase();
		else
			this.urlName=urlName;
		this.description=schema.description.getString(data);
		this.owners=schema.owners.getSequenceOrEmpty(model, data);
		this.members=schema.members.getSequenceOrEmpty(model, data);
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
			sj.add(r.get().username);
		return sj.toString();
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOwner(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }

	public boolean hasMember(User user) {
		if (members==null || user==null)
			return false;
		for (User.Ref r: members)
			if (r.refersTo(user)) 
				return true;
		return false;
	}

	public boolean hasOwner(User user) {
		if (owners==null || user==null)
			return false;
		for (User.Ref r: owners) {
			if (r.refersTo(user)) 
				return true;
		}
		return false;
	}

	public Comment findComment(String id) { 
		if (comments==null || id==null)
			return null;
		for (Comment com : comments)
			if (id.equals(com.id()))
				return com;
		return null;
	}

}