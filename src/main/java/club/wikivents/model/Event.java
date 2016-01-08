package club.wikivents.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.StringJoiner;

import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;

import com.github.jknack.handlebars.Handlebars.SafeString;

public class Event extends EventData implements Comparable<Event>, AccessChecker<User> {

	public Event(WikiventsModel model, Struct data) { super(model, data); }
	public Event(WikiventsModel model, User org, Struct data) {
		this(model, new MultiStruct(
			new SingleItemStruct(schema.organizers.name, ImmutableSequence.of(User.Ref.class, org.getRef())),
			new SingleItemStruct(schema.guests.name, ImmutableSequence.of(Guest.class, new Guest(org))),
			data
		));
	}

	public String guestCount() {
		if (guests==null)
			return "0";
		return guests.size()<=max ? guests.size()+"" : max+"+"+(guests.size()-max); 
	}   
	public String organizerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (User.Ref r : organizers)
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
	
	public boolean hasGuest(User user) { return user!=null && guests.hasItem(Guest.key,user._id); }
	public Guest findGuest(String id) { return guests.findItemOrNull(Guest.key, id); }


	private static ImmutableSequence.StringExpression userRefKey=(ref) -> {return ((User.Ref) ref).getKey(); };

	public boolean isLikedBy(User user) { return likes.hasItem(userRefKey, user._id); }


	
	
	public boolean hasOrganizer(User user) {
		if (organizers==null || user==null)
			return false;
		for (User.Ref r: organizers) {
			if (r.refersTo(user)) 
				return true;
		}
		return false;
	}

	public boolean hasGroup(Group gr) {
		if (groups==null || gr==null)
			return false;
		for (Group.Ref r: groups) {
			if (r.refersTo(gr)) 
				return true;
		}
		return false;
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
	
	public String urlify(String s) {
		return s.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^-a-zA-Z0-9]", "").replaceAll("\\-\\-+", "-");
	}
	public String urlEncode(String s) {
		if (s==null) return "";
		try {
			return URLEncoder.encode(s,"UTF-8");
		} 
		catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
	}
	
	private static Locale localeNl=new Locale("nl");
	private static ZoneId CET = ZoneId.of("CET");
	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd", localeNl);
	//private static DateTimeFormatter googleTimeFormatter = DateTimeFormatter.ofPattern("HHmmss", localeNl);
	public String formatGoogleDate(LocalDate date, LocalTime time) {
		if (time==null)
			return dateFormatter.format(date);
		LocalDateTime ldt=LocalDateTime.of(date, time);
		Instant inst = ldt.atZone(CET).toInstant();
		return inst.toString().replaceAll(":", "").replaceAll("-", "");
	}
	
	public LocalTime calcEndTime() {
		if (time==null)
			return null;
		if (endTime!=null)
			return endTime;
		return time.plusHours(1);
	}
	
	public LocalDate calcEndDate() {
		if (time==null)
			return date;
		if (calcEndTime().isAfter(time))
			return date;
		return date.plusDays(1);
	}
	
	public String googleCalendarUrl() {
		return "http://www.google.com/calendar/event?"
			+"action=TEMPLATE"
			+"&text="+urlEncode(title)
			+"&dates="+formatGoogleDate(date,time)+"%2f"+formatGoogleDate(calcEndDate(),calcEndTime())
			+"&details="+urlEncode("http://wikivents.nl/event/:"+_id)
			+"&location="+urlEncode(city)+",+"+urlEncode(location)
			+"&trp=false"
			//+"&ctz=CET"
			//+"&sprop="+urlEncode("website:http://wikivents.nl/event/:"+_id)
		;
	}
}
