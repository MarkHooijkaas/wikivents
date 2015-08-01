package club.wikivents.model;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudObjectSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

public class Event extends CrudObject implements Comparable<Event> {
	public final String title;
	public final String location;
	public final String description;
	public final LocalDate date;
	public final int min;
	public final int max;
	//public final CrudRef<User> organizer;
	public final Immutable.Sequence<User.Ref> organizers;
	public final Immutable.Sequence<Guest> guests;
	public final Immutable.Sequence<Comment> comments;
	//private final WikiventsModel model;
	
	public Event(WikiventsModel model, Struct data) {
		super(model.events, data);
		//System.out.println("Creating Event with "+props);
		this.title=schema.title.getString(data);
		this.description=schema.description.getString(data);
		this.location=schema.location.getString(data);
		this.date=schema.date.getLocalDate(data);
		this.min=schema.min.getInt(data);
		this.max=schema.max.getInt(data);
		String organizer = data.getString("organizer",null); 
		if (organizer==null)
			this.organizers=data.getTypedSequenceOrEmpty(model, User.Ref.class,"organizers");
		else
			this.organizers=Item.cast( Sequence.of(User.Ref.class, new User.Ref(model,organizer)));
		this.guests=schema.guests.getSequence(model, data);//data.getTypedSequenceOrEmpty(model, Guest.class,"guests");
		this.comments=schema.comments.getSequence(model, data);//data.getTypedSequenceOrEmpty(model, Comment.class,"comments");
		
	}
	
	public String guestCount() { return guests.size()<=max ? guests.size()+"" : max+"+"+(guests.size()-max); }   
	public String organizerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (CrudRef<User> r : organizers)
			sj.add(r.get().username);
		return sj.toString();
	}
	
	public static final Schema schema=new Schema();
	public static class Schema extends CrudObjectSchema<Event> {
		public Schema() { super(Event.class); addAllFields(); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField title = new StringField("title"); 
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(CrudRef.class,"organizers");
		public final IntField min = new IntField("min"); 
		public final IntField max = new IntField("max"); 
		public final LocalDateField date = new LocalDateField("date"); 
		public final StringField location = new StringField("location"); 
		public final StringField description = new StringField("description"); 
		public final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.class,"guests"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.class,"comments"); 
	}


	
	public boolean mayBeChangedBy(User user) { return user.isAdmin || isOrganizer(user); }
	
	public void addComment(WikiventsModel model, User user, String text) {
		Comment comment=new Comment(user,text);
		Event newEvent = this.modified(model, schema.comments, comments.growTail(comment));
		model.events.update(this, newEvent);
	}
	public boolean isGuest(User user) {
		for (Guest g : guests)
			if (g.user._id.equals(user._id)) // already member
				return true;
		return false;
	}
	public void addGuest(WikiventsModel model, User user) {
		if (isGuest(user))
			return;
		Guest guest=new Guest(model, user);
		Event newEvent = this.modified(model, schema.guests, guests.growTail(guest));
		model.events.update(this, newEvent);
	}
	public boolean isOrganizer(User user) {
		System.out.println("checking "+user);
		for (CrudRef<User> r: organizers) {
			if (r._id.equals(user._id)) // already member
				return true;
			System.out.println("not ");
		}
		System.out.println("not an organizer");
		return false;
	}

	public void addOrganizer(WikiventsModel model, User user) {
		if (isOrganizer(user))
			return;
		Event newEvent = this.modified(model, schema.organizers, organizers.growTail(new User.Ref(model, user._id)));
		System.out.println("newEvent"+newEvent);
		model.events.update(this, newEvent);
	}

	@Override public int compareTo(Event other) { return this.date.compareTo(other.date);}
}
