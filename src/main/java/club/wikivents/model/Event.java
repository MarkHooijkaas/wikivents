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

public class Event extends EventData implements Comparable<Event> {

	public Event(WikiventsModel model, Struct data) { super(model, data); }
	public Event(WikiventsModel model, User org, Struct data) {
		this(model, new MultiStruct(
			new SingleItemStruct(schema.owners.name, ImmutableSequence.of(User.Ref.class, org.getRef())),
			new SingleItemStruct(schema.members.name, ImmutableSequence.of(User.Ref.class, org.getRef())),
			data
		));
	}

	@Override public boolean mayBeViewedBy(User user) { return super.mayBeViewedBy(user) || hasInvitedGroupUser(user);}
	@Override public boolean mayBeJoinedBy(User user) { return super.mayBeJoinedBy(user) || hasInvitedGroupUser(user);}
	public boolean hasInvitedGroupUser(User.Ref user) {
		if (groups==null)
			return false;
		for (Group.Ref g: groups) {
			Group grp = g.get0();
			if (grp!=null && grp.hasOwner(user) || grp.hasMember(user) || grp.hasInvitedUser(user))
				return true;
		}
		return false;
	}
	public boolean hasInvitedGroupUser(User user) { return user!=null && hasInvitedGroupUser(user.getRef());}

	public String[] tagNames() { return tags.toLowerCase().split(",");}
	public Tag[] tagList() { return model.tags.tagList(tags);}
	public SafeString tagLinks() { return model.tags.tagLinks(tags);}

	public String dateKey() { 
		if (this.date==null) 
			return this.cancelled ? "0000-01-01" : "9999-12-31";
		return date+"";
	}
	public String memberCount() {
		String result="0";
		if (members!=null)
		result= members.size()<=max ? members.size()+"" : max+"+"+(members.size()-max); 
		if (max<9999 || !idea)
			result+="/"+max;
		return result;
	}   
	public String ownerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (User.Ref r : owners)
			sj.add(r.get().username);
		return sj.toString();
	}
	public SafeString ownerLinks() {
		StringJoiner sj = new StringJoiner("<br/>");
		for (User.Ref r : owners)
			sj.add(r.link());
		return new SafeString(sj.toString());
	}
	public ImmutableSequence<User.Ref> backupMembers() {
		if (members.size()<=max)
			return null;
		return members.subsequence(max);
	}
	public ImmutableSequence<User.Ref> allowedMembers() {
		if (members.size()<=max)
			return members;
		return members.subsequence(0,max);
	}
	
	public boolean allowNewMember() { return membersAllowed && members.size()<max && ! cancelled; }
	public boolean allowNewBackupMember() { return membersAllowed && backupMembersAllowed && ! cancelled; }
		
	//public Member findMember(String id) { return members.findItemOrNull(Member.key, id); }


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
	
	private static final DateTimeFormatter dateUrlFormat=DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public String getUrlPart() { 
		if (date==null)
			return "wikidee-"+urlName;
		return date.format(dateUrlFormat)+"-"+urlName; 
	}
	public String getUrl() { return "/event/"+ getUrlPart() + (date==null? "?id="+_id : "");}

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
