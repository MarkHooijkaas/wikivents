package club.wikivents.model;

import java.util.Date;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.struct4j.Struct;

public class Event extends CrudObject {
	public final String title;
	public final Date date;
	public final int min;
	public final int max;
	public final User organizer;
	
	public Event(WikiventsModel model, Struct s) {
		this(schema.organizer.get(model.users(), s), s);
	}

	@SuppressWarnings("deprecation")
	public Event(User organizer, Struct props) {
		super(schema, props);
		this.title=props.getString(schema.title.name);
		this.date=new Date(Date.parse(props.getString("date")));
		this.min=props.getInt(schema.min.name);
		this.max=props.getInt(schema.max.name);
		this.organizer=organizer;
	}
	
	public static Schema schema=new Schema();
	public static class Schema extends CrudSchema<Event> {
		public Schema() { super(Event.class); }
		public final StringField title = new StringField("title", false, null); 
		public final RefField<User> organizer = new RefField<User>("organizer", false);
		public final IntField min = new IntField("min", false, 0); 
		public final IntField max = new IntField("max", false, 0); 
	}

}
