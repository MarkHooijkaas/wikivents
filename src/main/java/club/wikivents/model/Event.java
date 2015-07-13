package club.wikivents.model;

import java.util.Date;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.crud4j.StructStorage;
import org.kisst.crud4j.impl.BaseTable;
import org.kisst.item4j.struct.Struct;

public class Event extends CrudObject {
	public final String title;
	public final Date date;
	public final int min;
	public final int max;
	public final CrudTable.Ref<User> organizer;
	//public final TypedSequence<CrudTable<User>.Ref> guests;
	
	public Event(WikiventsModel model, Struct s) {
		this(schema.organizer.get(model.users,s), s);
	}

	public Event(CrudTable.Ref<User> organizer, Struct props) {
		super(schema, props);
		this.title=schema.title.getValue(props);
		this.date=schema.date.getValue(props);
		this.min=schema.min.getValue(props);
		this.max=schema.max.getValue(props);
		this.organizer=organizer;
		//this.guests=new TypedArraySequence<CrudTable<User>.Ref>(props.getSequence("guests"));
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudSchema<Event> {
		public Schema() { super(Event.class); addAllFields(); }
		public final StringField title = new StringField(Event.class, "title"); 
		public final RefField<User> organizer = new RefField<User>(Event.class,"organizer");
		public final IntField min = new IntField(Event.class, "min"); 
		public final IntField max = new IntField(Event.class, "max"); 
		public final DateField date = new DateField(Event.class, "date"); 
		//@Override public Event createObject(Struct doc) { return new Event(?table?, doc); }
	}

	public static class Table extends BaseTable<Event> {
		private final WikiventsModel model;
		public Table(StructStorage storage, WikiventsModel model) { 
			super(Event.schema, storage); 
			this.model=model;
		}
		@Override public Event createObject(Struct doc) { return new Event(model, doc); }
	}
}