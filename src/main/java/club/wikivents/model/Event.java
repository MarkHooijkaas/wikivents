package club.wikivents.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.StringJoiner;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.Handlebars.SafeString;

public class Event extends CrudObject implements Comparable<Event>, AccessChecker<User> {
	public final String title;
	public final String city;
	public final String location;
	public final String cost;
	public final String description;
	public final String guestInfo;
	public final String imageUrl;
	public final LocalDate date;
	public final LocalTime time;
	public final LocalTime endTime;
	public final int max;
	public final boolean guestsAllowed;
	public final boolean backupGuestsAllowed;
	public final boolean cancelled;
	public final boolean idea;
	public final ImmutableSequence<User.Ref> organizers;
	public final ImmutableSequence<User.Ref> likes;
	public final ImmutableSequence<Group.Ref> groups;
	public final ImmutableSequence<Guest> guests;
	public final ImmutableSequence<Comment> comments;
	
	public Event(WikiventsModel model, User org, Struct data) {
		this(model, new MultiStruct(
			new SingleItemStruct(schema.organizers.name, ImmutableSequence.of(User.Ref.class, new User.Ref(model, org._id))),
			new SingleItemStruct(schema.guests.name, ImmutableSequence.of(Guest.class, new Guest(model, org))),
			data
		));
	}
	public Event(WikiventsModel model, Struct data) {
		super(model.events, data);
		this.title=schema.title.getString(data);
		this.description=schema.description.getString(data);
		this.guestInfo=schema.guestInfo.getString(data, null);
		this.imageUrl=schema.imageUrl.getString(data);
		this.city=schema.city.getString(data);
		this.location=schema.location.getString(data);
		this.cost=schema.cost.getString(data);
		this.date=schema.date.getLocalDate(data);
		this.time=schema.time.getLocalTime(data);
		this.endTime=schema.endTime.getLocalTime(data);
		this.max=schema.max.getInt(data);
		this.guestsAllowed=schema.guestsAllowed.getBoolean(data,true);
		this.backupGuestsAllowed=schema.backupGuestsAllowed.getBoolean(data,true);
		this.cancelled=schema.cancelled.getBoolean(data,false);
		this.idea=schema.idea.getBoolean(data,false);
		this.organizers=schema.organizers.getSequenceOrEmpty(model, data);
		this.likes=schema.likes.getSequenceOrEmpty(model, data);
		this.guests=schema.guests.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
		this.groups=schema.groups.getSequenceOrEmpty(model, data);
	}
	
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudSchema<Event> {
		private Schema() { super(Event.class); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField imageUrl = new StringField("imageUrl"); 
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(User.Ref.type,"organizers");
		public final SequenceField<User.Ref> likes = new SequenceField<User.Ref>(User.Ref.type,"likes");
		public final IntField max = new IntField("max"); 
		public final BooleanField guestsAllowed = new BooleanField("guestsAllowed");
		public final BooleanField backupGuestsAllowed = new BooleanField("backupGuestsAllowed");
		public final BooleanField cancelled = new BooleanField("cancelled");
		public final BooleanField idea = new BooleanField("idea");
		public final LocalDateField date = new LocalDateField("date"); 
		public final LocalTimeField time = new LocalTimeField("time"); 
		public final LocalTimeField endTime = new LocalTimeField("endTime"); 
		public final StringField city = new StringField("city"); 
		public final StringField location = new StringField("location"); 
		public final StringField cost = new StringField("cost"); 
		public final StringField description = new StringField("description"); 
		public final StringField guestInfo = new StringField("guestInfo"); 
		public final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.schema,"guests"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.schema,"comments");
		public final SequenceField<Group.Ref> groups = new SequenceField<Group.Ref>(Group.Ref.type,"groups");
	}

	public String guestCount() {
		if (guests==null)
			return "0";
		return guests.size()<=max ? guests.size()+"" : max+"+"+(guests.size()-max); 
	}   
	public String organizerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (CrudRef<User> r : organizers)
			sj.add(r.get().username);
		return sj.toString();
	}
	public SafeString organizerLinks() {
		StringJoiner sj = new StringJoiner("<br/>");
		for (User.Ref r : organizers)
			sj.add(r.link());
		return new SafeString(sj.toString());
	}
	public ImmutableSequence<Guest> backupGuests() {
		if (guests.size()<=max)
			return null;
		return guests.subsequence(max);
	}
	public ImmutableSequence<Guest> allowedGuests() {
		if (guests.size()<=max)
			return guests;
		return guests.subsequence(0,max);
	}
	
	public boolean allowNewGuest() { return guestsAllowed && guests.size()<max && ! cancelled; }
	public boolean allowNewBackupGuest() { return guestsAllowed && backupGuestsAllowed && ! cancelled; }
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOrganizer(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }
	
	public void addComment(WikiventsModel model, User user, String text) {
		model.events.addSequenceItem(this, schema.comments, new Comment(user,text));
	}
	
	public boolean hasGuest(User user) { return guests.hasItem(Guest.key,user._id); }
	public Guest findGuest(String id) { return guests.findItemOrNull(Guest.key, id); }
	public void addGuest(WikiventsModel model, User user) { 
		if (! hasGuest(user))
			model.events.addSequenceItem(this, schema.guests, new Guest(model, user));
	}
	public void removeGuest(WikiventsModel model, String id) {
		model.events.removeSequenceItem(this, schema.guests, findGuest(id));
	}

	private static ImmutableSequence.StringExpression userRefKey=(ref) -> {return ((User.Ref) ref)._id; };

	public boolean isLikedBy(User user) { return likes.hasItem(userRefKey, user._id); }
	public void addLike(WikiventsModel model, User user) {
		if (! isLikedBy(user))
			model.events.addSequenceItem(this, schema.likes, new User.Ref(model, user._id));
	}
	public void removeLike(WikiventsModel model, User user) {
		User.Ref ref = new User.Ref(model, user._id);
		model.events.removeSequenceItem(this, schema.likes, ref);
	}

	
	
	public boolean hasOrganizer(User user) {
		if (organizers==null || user==null)
			return false;
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
	public void removeOrganizer(WikiventsModel model, User user) {
		if (organizers.size()==1) // never remove the last organizer
			return;
		User.Ref ref = new User.Ref(model, user._id);
		model.events.removeSequenceItem(this, schema.organizers, ref);
	}

	public boolean hasGroup(Group gr) {
		if (groups==null || gr==null)
			return false;
		for (Group.Ref r: groups) {
			if (r._id.equals(gr._id)) 
				return true;
		}
		return false;
	}
	public void addGroup(WikiventsModel model, Group gr) {
		if (hasGroup(gr))
			return;
		model.events.addSequenceItem(this, schema.groups, new Group.Ref(model, gr._id));
	}
	public void removeGroup(WikiventsModel model, Group gr) {
		Group.Ref ref = new Group.Ref(model, gr._id);
		model.events.removeSequenceItem(this, schema.groups, ref);
	}

	
	@Override public int compareTo(Event other) { return this.date.compareTo(other.date);}

	public Comment findComment(String id) { 
		if (comments==null || id==null)
			return null;
		for (Comment com : comments)
			if (id.equals(com.id()))
				return com;
		return null;
	}
}
