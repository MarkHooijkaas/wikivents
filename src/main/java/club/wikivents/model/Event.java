package club.wikivents.model;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudObjectSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.Immutable;
import org.kisst.item4j.Immutable.Sequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.Handlebars.SafeString;

public class Event extends CrudObject implements Comparable<Event>, AccessChecker<User> {
	public final String title;
	public final String location;
	public final String description;
	public final LocalDate date;
	public final int min;
	public final int max;
	public final Immutable.Sequence<User.Ref> organizers;
	public final Immutable.Sequence<Guest> guests;
	public final Immutable.Sequence<Comment> comments;
	
	public Event(WikiventsModel model, Struct data) {
		super(model.events, data);
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
		this.guests=schema.guests.getSequence(model, data);
		this.comments=schema.comments.getSequence(model, data);
		
	}
	@Override public String getUniqueSortingKey() { return ""+date+"|"+_id; }

	public String guestCount() { return guests.size()<=max ? guests.size()+"" : max+"+"+(guests.size()-max); }   
	public String organizerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (CrudRef<User> r : organizers)
			sj.add(r.get().username);
		return sj.toString();
	}
	public SafeString organizerLinks() {
		StringJoiner sj = new StringJoiner(", ");
		for (User.Ref r : organizers)
			sj.add(r.link());
		return new SafeString(sj.toString());
	}
	
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudObjectSchema<Event> {
		private Schema() { super(Event.class); addAllFields(); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField title = new StringField("title"); 
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(User.Ref.type,"organizers");
		public final IntField min = new IntField("min"); 
		public final IntField max = new IntField("max"); 
		public final LocalDateField date = new LocalDateField("date"); 
		public final StringField location = new StringField("location"); 
		public final StringField description = new StringField("description"); 
		public final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.schema,"guests"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.schema,"comments"); 
	}


	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOrganizer(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }
	
	public void addComment(WikiventsModel model, User user, String text) {
		model.events.addSequenceItem(this, schema.comments, new Comment(user,text));
	}
	public boolean hasGuest(User user) {
		for (Guest g : guests)
			if (g.user._id.equals(user._id)) // already member
				return true;
		return false;
	}
	public Guest findGuest(User user) {
		for (Guest g : guests)
			if (g.user._id.equals(user._id)) // already member
				return g;
		return null;
	}
	public void addGuest(WikiventsModel model, User user) {
		if (hasGuest(user))
			return;
		model.events.addSequenceItem(this, schema.guests, new Guest(model, user));
	}
	public void removeGuest(WikiventsModel model, User user) {
		Guest g=findGuest(user);
		if (g!=null)
			model.events.removeSequenceItem(this, schema.guests, g);
	}
	public boolean hasOrganizer(User user) {
		for (User.Ref r: organizers) {
			if (r._id.equals(user._id)) 
				return true;
		}
		return false;
	}

	public void addOrganizer(WikiventsModel model, User user) {
		if (hasOrganizer(user))
			return;
		model.events.addSequenceItem(this, schema.organizers, new User.Ref(model, user._id));
	}

	@Override public int compareTo(Event other) { return this.date.compareTo(other.date);}

}
