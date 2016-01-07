package club.wikivents.model;

import java.util.ArrayList;
import java.util.StringJoiner;

import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;

public class Group extends GroupData implements AccessChecker<User> {

	public Group(WikiventsModel model, Struct data) { super(model, data); }
	public Group(WikiventsModel model, User org, Struct data) {
		this(model, new MultiStruct(
			new SingleItemStruct(schema.owners.name, ImmutableSequence.of(User.Ref.class, org.getRef())),
			new SingleItemStruct(schema.members.name, ImmutableSequence.of(User.Ref.class, org.getRef())),
			data
		));
	}

	public String ownerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (User.Ref r : owners)
			sj.add(r.get().username);
		return sj.toString();
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOwner(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }

	public boolean hasMember(User user) {
		if (members==null || user==null)
			return false;
		for (User.Ref r: members)
			if (r._id.equals(user._id)) 
				return true;
		return false;
	}

	public boolean hasOwner(User user) {
		if (owners==null || user==null)
			return false;
		for (User.Ref r: owners) {
			if (r._id.equals(user._id)) 
				return true;
		}
		return false;
	}

	public Comment findComment(String id) { 
		if (comments==null || id==null)
			return null;
		for (Comment com : comments)
			if (id.equals(com.id()))
				return com;
		return null;
	}
	
	public ArrayList<Event> futureEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: table.model.futureEvents()) {
			if (e.hasGroup(this))
				result.add(e);
		}
		return result;
	}
	public ArrayList<Event> pastEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: table.model.pastEvents()) {
			if (e.hasGroup(this))
				result.add(e);
		}
		return result;
	}
}
