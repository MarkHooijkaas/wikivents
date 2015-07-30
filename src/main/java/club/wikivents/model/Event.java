package club.wikivents.model;

import java.time.LocalDate;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudObjectSchema;
import org.kisst.crud4j.CrudRef;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class Event extends CrudObject implements Comparable<Event> {
	public final String title;
	public final String location;
	public final String description;
	public final LocalDate date;
	public final int min;
	public final int max;
	public final CrudRef<User> organizer;
	public final Immutable.Sequence<Guest> guests;
	public final Immutable.Sequence<Comment> comments;
	//private final WikiventsModel model;
	
	public Event(WikiventsModel model, Struct props) {
		super(props);
		//System.out.println("Creating Event with "+props);
		this.title=schema.title.getString(props);
		this.description=schema.description.getString(props);
		this.location=schema.location.getString(props);
		this.date=schema.date.getLocalDate(props);
		this.min=schema.min.getInt(props);
		this.max=schema.max.getInt(props);
		this.organizer=schema.organizer.getRef(model.users, props);//new SimpleRef(props.getString("organizer",null));
		this.guests=props.getTypedSequence(Guest.class,"guests", null);
		this.comments=props.getTypedSequence(Comment.class,"comments", null);
	}

	public static final Schema schema=new Schema();
	public static class Schema extends CrudObjectSchema<Event> {
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


	
	@Override public boolean mayBeChangedBy(String userId) { return _id.equals(userId); }
	
	public void addComment(WikiventsModel model, User user, String text) {
		HashStruct data=new HashStruct(); 
		data.put("comment", text);
		data.put("user", user._id);
		Comment comment=new Comment(model,data);
		Immutable.Sequence<Comment> newComments = null;
		if (comments==null)
			newComments=Immutable.typedSequence(Comment.class, comment);
		else
			newComments=comments.growTail(comment);
		HashStruct newEventData=new HashStruct(); 
		newEventData.put("comments", newComments);
		Event newEvent = new Event(model, new MultiStruct(newEventData, this));
		model.events.update(this, newEvent);
	}
	public void addGuest(WikiventsModel model, User user) {
		HashStruct data=new HashStruct(); 
		data.put("user", user._id);
		Guest guest=new Guest(model, data);
		Immutable.Sequence<Guest> newSeq = null;
		if (guests==null)
			newSeq=Immutable.typedSequence(Guest.class, guest);
		else
			newSeq=guests.growTail(guest);
		HashStruct newEventData=new HashStruct(); 
		newEventData.put("guests", newSeq);
		Event newEvent = new Event(model, new MultiStruct(newEventData, this));
		model.events.update(this, newEvent);
	}

	@Override public int compareTo(Event other) { return this.date.compareTo(other.date);}
}
