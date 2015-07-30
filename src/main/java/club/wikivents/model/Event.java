package club.wikivents.model;

import java.time.Instant;
import java.time.LocalDate;

import org.kisst.item4j.Immutable;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.ReflectStruct;
import org.kisst.item4j.struct.Struct;

public class Event extends ReflectStruct implements Comparable<Event> {
	public final String _id;
	public final String title;
	public final String location;
	public final String description;
	public final LocalDate date;
	public final int min;
	public final int max;
	public final String organizer;
	public final Immutable.Sequence<Guest> guests;
	public final Immutable.Sequence<Comment> comments;
	//private final WikiventsModel model;
	
	public Event(Struct data) {
		//System.out.println("Creating Event with "+props);
		this._id=data.getString("_id");
		this.title=data.getString("title");
		this.description=data.getString("description");
		this.location=data.getString("location");
		this.date=data.getLocalDate("date");
		this.min=data.getInteger("min");
		this.max=data.getInteger("max");
		this.organizer=data.getString("_id");
		this.guests=null;//props.getTypedSequence(Guest.class,"guests", null);
		this.comments=null;//props.getTypedSequence(Comment.class,"comments", null);
	}


	public static class Guest {
		public final String user;
		public final Instant date;
		public Guest(Struct data) {
			this.user=data.getString("user");
			this.date=data.getInstant("date",Instant.now());
		}
	}
	public static class Comment extends ReflectStruct {
		public final String user;
		public final Instant date;
		public final String comment;
		public Comment(Struct data) {
			this.user=data.getString("user");
			this.date=data.getInstant("date",Instant.now());
			this.comment=data.getString("comment");
		}
	}

	
	
	public void addComment(WikiventsModel model, User user, String text) {
		HashStruct data=new HashStruct(); 
		data.put("comment", text);
		data.put("user", user._id);
		Event.Comment comment=new Event.Comment(data);
		Immutable.Sequence<Comment> newComments = null;
		if (comments==null)
			newComments=Immutable.typedSequence(Comment.class, comment);
		else
			newComments=comments.growTail(comment);
		HashStruct newEventData=new HashStruct(); 
		newEventData.put("comments", newComments);
		Event newEvent = new Event(new MultiStruct(newEventData, this));
		model.events.update(this, newEvent);
	}
	public void addGuest(WikiventsModel model, User user) {
		HashStruct data=new HashStruct(); 
		data.put("user", user._id);
		Event.Guest guest=new Event.Guest( data);
		Immutable.Sequence<Guest> newSeq = null;
		if (guests==null)
			newSeq=Immutable.typedSequence(Guest.class, guest);
		else
			newSeq=guests.growTail(guest);
		HashStruct newEventData=new HashStruct(); 
		newEventData.put("guests", newSeq);
		Event newEvent = new Event(new MultiStruct(newEventData, this));
		model.events.update(this, newEvent);
	}

	@Override public int compareTo(Event other) { return this.date.compareTo(other.date);}
}
