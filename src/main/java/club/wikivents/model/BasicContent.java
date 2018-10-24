package club.wikivents.model;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoSchema;
import org.kisst.pko4j.PkoTable;
import org.kisst.util.StringUtil;

public abstract class BasicContent<T extends BasicContent<T>> extends WikiventsObject<T> implements Item.Factory {
	public static Schema<Wikivent> contentSchema =new Schema<>(Wikivent.class); //TODO: have a schema with a neutral class
	public static class Schema<T extends PkoObject> extends PkoSchema<T> {
		protected Schema(Class<T> cls) { super(cls); }
		public final IdField _id = new IdField();
		public final InstantField creationDate = new InstantField("creationDate");
		public final InstantField modificationDate = new InstantField("modificationDate");
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField name = new StringField("name");
		public final StringField urlName= new StringField("urlName");
		public final SequenceField<User.Ref> likes = new SequenceField<User.Ref>(User.Ref.class,"likes");
		public final StringField content = new StringField("content");
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.class,"comments");
	}

	public final String name;
	public final String urlName;
	public final String content;
	public final ImmutableSequence<User.Ref> likes;
	public final ImmutableSequence<Comment> comments;

	public static class OldEventSchema<T extends PkoObject> extends PkoSchema<T> {
		private OldEventSchema(Class<T> cls) { super(cls); }
		private final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.class,"guests");
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(User.Ref.class,"organizers");
	}
	private static OldEventSchema<Wikivent> oldEventSchema = new OldEventSchema<Wikivent>(Wikivent.class);

	public BasicContent(Schema<T> schema, WikiventsModel model, PkoTable<T> table, Struct data) {
		super(model, table, data);
		this.name=schema.name.getString(data);
		String urlName=schema.urlName.getString(data);
		if (urlName==null)
			this.urlName=StringUtil.urlify(name).toLowerCase();
		else
			this.urlName=urlName;
		this.content=schema.content.getString(data);
		this.likes=schema.likes.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
	}
	

	@Override public String getName() { return name; }

	@Override public abstract boolean mayBeChangedBy(User user);
	@Override public abstract boolean mayBeViewedBy(User user);

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
