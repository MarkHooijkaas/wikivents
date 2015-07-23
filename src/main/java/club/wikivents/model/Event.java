package club.wikivents.model;

import java.time.Instant;
import java.time.LocalDate;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Immutable;
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
	public static class Comment {
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
			this.date=schema.date.getInstant(props);
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
	
	public Event(WikiventsModel model, Struct props) {
		super(schema, props);
		this.title=schema.title.getString(props);
		this.description=schema.description.getString(props);
		this.location=schema.location.getString(props);
		this.date=schema.date.getLocalDate(props);
		this.min=schema.min.getInt(props);
		this.max=schema.max.getInt(props);
		this.organizer=schema.organizer.getRef(model.users,props);//new SimpleRef(props.getString("organizer",null));
		this.guests=props.getTypedSequence(Guest.class,"guests", null);
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
		public final StringField guests= new StringField("description"); 
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
}
