package club.wikivents.model;

import java.time.Instant;
import java.time.LocalDate;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Event extends CrudObject {
	public static class Guest {
		public final CrudTable<User>.Ref user;
		public final Instant date;
		public static final Schema schema=new Schema();
		public static class Schema extends CrudSchema<Guest> {
			public Schema() { super(Guest.class); addAllFields(); }
			public final RefField<User> user = new RefField<User>("user");
			public final InstantField date = new InstantField("date"); 
		}
		public Guest(WikiventsModel model, Struct props) {
			this.user=schema.user.getRef(model.users,props);
			this.date=schema.date.getInstant(props);
		}
	}
	public static class Comment extends ReflectStruct {
		public final CrudTable<User>.Ref user;
		public final Instant date;
		public final String comment;
		public static final Schema schema=new Schema();
		public static class Schema extends CrudSchema<Comment> {
			public Schema() { super(Comment.class); addAllFields(); }
			public final RefField<User> user = new RefField<User>("user");
			public final InstantField date = new InstantField("date"); 
			public final StringField comment = new StringField("comment"); 
		}
		public Comment(WikiventsModel model, Struct props) {
			this.user=schema.user.getRef(model.users,props);
			Instant tmpdate = schema.date.getInstant(props);
			if (tmpdate==null)
				this.date=Instant.now();
			else
				this.date=tmpdate;
			this.comment=schema.comment.getString(props);
		}
	}

	
	public final String title;
	public final String location;
	public final String description;
	public final LocalDate date;
	public final int min;
	public final int max;
	public final CrudTable<User>.Ref organizer;
	public final Immutable.Sequence<Guest> guests;
	public final Immutable.Sequence<Comment> comments;
	private final WikiventsModel model;
	
	public Event(WikiventsModel model, Struct props) {
		super(schema, props);
		this.model=model;
		this.title=schema.title.getString(props);
		this.description=schema.description.getString(props);
		this.location=schema.location.getString(props);
		this.date=schema.date.getLocalDate(props);
		this.min=schema.min.getInt(props);
		this.max=schema.max.getInt(props);
		this.organizer=schema.organizer.getRef(model.users,props);//new SimpleRef(props.getString("organizer",null));
		this.guests=props.getTypedSequence(Guest.class,"guests", null);
		this.comments=props.getTypedSequence(Comment.class,"comments", null);
	}
	public boolean mayBeChangedBy(String userId) { return userId!=null && userId.equals(organizer._id); }

	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Event> {
		public Schema() { super(Event.class); addAllFields(); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField title = new StringField("title"); 
		public final RefField<User> organizer = new RefField<User>("organizer");
		public final IntField min = new IntField("min"); 
		public final IntField max = new IntField("max"); 
		public final LocalDateField date = new LocalDateField("date"); 
		public final StringField location = new StringField("location"); 
		public final StringField description = new StringField("description"); 
		public final StringField guests= new StringField("guests"); // TODO: these are not String fields 
		public final StringField comments= new StringField("comments"); 
	}

	public static class Table extends CrudTable<Event> {
		public class Ref extends CrudTable<Event>.Ref { public Ref(String id) { super(id); } }
		@Override public Ref createRef(String _id) { return new Ref(_id); }

		//private final WikiventsModel model;
		public Table(StructStorage storage, WikiventsModel model) { 
			super(Event.schema, model, storage);
			System.out.println("Model is "+model);
			//this.model=model;
		}
		@Override public Event createObject(Struct doc) { return new Event((WikiventsModel) factory, doc); }
	}

	public void addComment(User user, String text) {
		HashStruct data=new HashStruct(); 
		data.put(Event.Comment.schema.comment.getName(), text);
		data.put(Event.Comment.schema.user.getName(), "User("+user._id+")"); // TODO: ugly...
		Event.Comment comment=new Event.Comment(model, data);
		Immutable.Sequence<Comment> newComments = null;
		if (comments==null)
			newComments=Immutable.typedSequence(Comment.class, comment);
		else
			newComments=comments.growTail(comment);
		HashStruct newEventData=new HashStruct(); 
		newEventData.put(Event.schema.comments.getName(), newComments);
		System.out.println(newComments);
		Event newEvent = new Event(model, new MultiStruct(newEventData, this));
		model.events.update(this, newEvent);
	}
}
