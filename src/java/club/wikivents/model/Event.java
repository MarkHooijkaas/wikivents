package club.wikivents.model;

import java.util.Date;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable;
import org.kisst.struct4j.Struct;
//import org.kisst.struct4j.seq.TypedSequence;

public class Event extends CrudObject {
	public final String title;
	public final Date date;
	public final int min;
	public final int max;
	public final CrudTable.Ref<User> organizer;
	//public final TypedSequence<CrudTable<User>.Ref> guests;
	
	public Event(WikiventsModel model, Struct s) {
		this(schema.organizer.get(model.users(), s), s);
	}

	@SuppressWarnings("deprecation")
	public Event(CrudTable.Ref<User> organizer, Struct props) {
		super(schema, props);
		this.title=props.getString(schema.title.name);
		this.date=new Date(Date.parse(props.getString("date")));
		this.min=props.getInt(schema.min.name);
		this.max=props.getInt(schema.max.name);
		this.organizer=organizer;
		//this.guests=new TypedArraySequence<CrudTable<User>.Ref>(props.getSequence("guests"));
	}
	
	public static Schema schema=new Schema();
	public static class Schema extends CrudSchema<Event> {
		public Schema() { super(Event.class); addAllFields(); }
		public final StringField title = new StringField(Event.class, "title", false, null); 
		public final RefField<User> organizer = new RefField<User>(Event.class, "organizer", false);
		public final IntField min = new IntField(Event.class, "min", false, 0); 
		public final IntField max = new IntField(Event.class, "max", false, 0); 
	}

	public interface Table extends CrudTable<Event> {
		public MultiIndex<Event>  organiserIndex();
		public OrderedIndex<Event> dateIndex();
	}
}
