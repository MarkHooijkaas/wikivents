package club.wikivents.model;

import java.util.Date;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.struct.Struct;

public class Event extends CrudObject {
	public static class Guest extends CrudObject {
		public final User.Table.Ref user;
		public final Date date;
		public static final Schema schema=new Schema();
		public static class Schema extends CrudSchema<Guest> {
			public Schema() { super(Guest.class); addAllFields(); }
			public final User.Table.RefField user = new User.Table.RefField("user");
			public final DateField date = new DateField(Guest.class,"date"); 
		}
		public Guest(WikiventsModel model, Struct props) {
			super(schema, props);
			this.user=null;//schema.user.get(model.users,props);
			this.date=schema.date.getValue(props);
		}

	}
	
	public final String title;
	public final String location;
	public final String description;
	public final Date date;
	public final int min;
	public final int max;
	public final User.Table.Ref organizer;
	public final Immutable.Sequence<Guest> guests;
	
	public Event(WikiventsModel model, Struct props) {
		super(schema, props);
		this.title=schema.title.getValue(props);
		this.description=schema.description.getValue(props);
		this.location=schema.location.getValue(props);
		this.date=schema.date.getValue(props);
		this.min=schema.min.getValue(props);
		this.max=schema.max.getValue(props);
		this.organizer=null;//schema.organizer.get(model.users,props);
		this.guests=props.getTypedSequence(Guest.class,"guests");
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Event> {
		public Schema() { super(Event.class); addAllFields(); }
		public final StringField title = new StringField(Event.class, "title"); 
		public final User.Table.RefField organizer = new User.Table.RefField("organizer");
		public final IntField min = new IntField(Event.class, "min"); 
		public final IntField max = new IntField(Event.class, "max"); 
		public final DateField date = new DateField(Event.class, "date"); 
		public final StringField location = new StringField(Event.class, "location"); 
		public final StringField description = new StringField(Event.class, "description"); 
	}

	public static class Table extends CrudTable<Event> {
		public class Ref extends CrudTable<Event>.Ref { public Ref(String id) { super(id); } }
		@Override public Ref createRef(String _id) { return new Ref(_id); }

		private final WikiventsModel model;
		public Table(StructStorage storage, WikiventsModel model) { 
			super(Event.schema, storage); 
			this.model=model;
		}
		@Override public Event createObject(Struct doc) { return new Event(model, doc); }
	}
}
