package club.wikivents.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.item4j.struct.StructProps;
import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoRef;
import org.kisst.pko4j.PkoSchema;
import org.kisst.pko4j.PkoModel.MyObject;
import org.kisst.props4j.Props;

public class EventData extends BasicPkoObject<WikiventsModel, Event> implements Item.Factory {
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	public static final Schema schema=new Schema();
	public static final class Schema extends PkoSchema<Event> {
		private Schema() { super(Event.class); }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField imageUrl = new StringField("imageUrl"); 
		public final SequenceField<User.Ref> organizers = new SequenceField<User.Ref>(User.Ref.class,"organizers");
		public final SequenceField<User.Ref> likes = new SequenceField<User.Ref>(User.Ref.class,"likes");
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
		public final SequenceField<Guest> guests= new SequenceField<Guest>(Guest.class,"guests"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.class,"comments");
		public final SequenceField<Group.Ref> groups = new SequenceField<>(Group.Ref.class,"groups");
		public final SequenceField<Poll> polls = new SequenceField<>(Poll.class,"polls");
	}
	
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
	public final ImmutableSequence<Poll> polls;
	
	public EventData(WikiventsModel model, Struct data) {
		super(model, model.events, data);
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
		this.polls=schema.polls.getSequenceOrEmpty(this, data);
	}
	
	@Override public Ref getRef() { return Ref.of(model,_id); }
	public static class Ref extends PkoRef<Event> {
		static public Ref of(WikiventsModel model, String key) { return key==null ? null : new Ref(model, key); }
		public static class Field extends Schema.BasicField<Event.Ref> {
			public Field(String name) { super(Event.Ref.class, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return of(model, Item.asString(data.getDirectFieldValue(name)));}
		}
		private Ref(WikiventsModel model, String _id) { super(model.events, _id); }
	}
	@Override public String getName() { return title; }

	public class Poll extends ReflectStruct implements MyObject {
		public final String title;
		public final String description;
		public final ImmutableSequence<Option> options;
		public Poll(Struct data) {
			Props props=StructProps.of(data);
			this.title=props.getString("title");
			this.description=props.getString("description");
			this.options=props.getTypedSequenceOrEmpty(model, Option.class, "options");
		}
		
		public class Option {
			public final String title;
			public final String description;
			public final ImmutableSequence<Answer> answers;
			public Option(Struct data) {
				Props props=StructProps.of(data);
				this.title=props.getString("title");
				this.description=props.getString("description");
				this.answers=props.getTypedSequenceOrEmpty(model, Answer.class, "answers");
			}

			public class Answer {
				public final User.Ref user;
				public final PollAnswer possible;
				public final String comment;
				public Answer(Struct data) {
					Props props=StructProps.of(data);
					this.comment=props.getString("title",null);
					this.user=null;
					this.possible=PollAnswer.valueOf(props.getString("possible"));
				}
			}
		}
	}
	enum PollAnswer { YES, NO, MAYBE }

	@Override public Object construct(Class<?> cls, Object data) {
		if (Poll.class==cls && data instanceof Struct)
			return this.new Poll((Struct)data);
		return model.construct(cls, data);
	};
}
