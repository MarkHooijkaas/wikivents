package club.wikivents.model;

import java.util.ArrayList;

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

	public String getUrl() { return "/group/"+urlName; }
	
	public ArrayList<Event> futureEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.futureEvents()) {
			if (e.hasGroup(this))
				result.add(e);
		}
		return result;
	}
	public ArrayList<Event> pastEvents() {
		ArrayList<Event> result=new ArrayList<Event>();
		for (Event e: model.pastEvents()) {
			if (e.hasGroup(this))
				result.add(e);
		}
		return result;
	}
}
