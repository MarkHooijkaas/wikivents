package club.wikivents.model;

import java.util.ArrayList;
import java.util.StringJoiner;

import org.kisst.crud4j.CrudModelObject;
import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.crud4j.CrudTable.CrudRef;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.Type;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.SingleItemStruct;
import org.kisst.item4j.struct.Struct;

public class Group extends CrudObject implements AccessChecker<User> {
	public final WikiventsModel model;
	public final String title;
	public final String description;
	public final ImmutableSequence<User.Ref> owners;
	public final ImmutableSequence<User.Ref> members;
	public final ImmutableSequence<Comment> comments;
	
	public Group(WikiventsModel model, User org, Struct data) {
		this(model, new MultiStruct(
			new SingleItemStruct(schema.owners.name, ImmutableSequence.of(User.Ref.class, new User.Ref(model, org._id))),
			new SingleItemStruct(schema.members.name, ImmutableSequence.of(User.Ref.class, new User.Ref(model, org._id))),
			data
		));
	}
	public Group(WikiventsModel model, Struct data) {
		super(model.groups, data);
		this.model=model;
		this.title=schema.title.getString(data);
		this.description=schema.description.getString(data);
		this.owners=schema.owners.getSequenceOrEmpty(model, data);
		this.members=schema.members.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
		
	}
	
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudSchema<Group> {
		private Schema() { super(Group.class); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final StringField title = new StringField("title"); 
		public final StringField description = new StringField("description"); 
		public final SequenceField<User.Ref> owners = new SequenceField<User.Ref>(User.Ref.type,"owners");
		public final SequenceField<User.Ref> members= new SequenceField<User.Ref>(User.Ref.type,"members"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.schema,"comments");
	}

	public static class Ref extends CrudRef<Group> implements CrudModelObject {
		public static final Type<Group.Ref> type = new Type.Java<Group.Ref>(Group.Ref.class, null); // XXX TODO: parser is null 
		public static class Field extends Schema.BasicField<Group.Ref> {
			public Field(String name) { super(Group.Ref.type, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return new Group.Ref(model, Item.asString(data.getDirectFieldValue(name)));}
		}
		public Ref(WikiventsModel model, String _id) { super(model.groups, _id); }
	}

	
	
	public String ownerNames() {
		StringJoiner sj = new StringJoiner(", ");
		for (CrudRef<User> r : owners)
			sj.add(r.get().username);
		return sj.toString();
	}
	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOwner(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }
	
	public void addComment(WikiventsModel model, User user, String text) {
		model.groups.addSequenceItem(this, schema.comments, new Comment(user,text));
	}
	public boolean hasMember(User user) {
		if (members==null || user==null)
			return false;
		for (User.Ref r: members)
			if (r._id.equals(user._id)) 
				return true;
		return false;
	}
	public void addMember(WikiventsModel model, User user) {
		if (hasMember(user))
			return;
		model.groups.addSequenceItem(this, schema.members, new User.Ref(model, user._id));
	}
	public void removeMember(WikiventsModel model, String id) {
		User.Ref ref = new User.Ref(model, id);
		model.groups.removeSequenceItem(this, schema.members, ref);
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

	public void addOwner(WikiventsModel model, User user) {
		if (hasOwner(user))
			return;
		model.groups.addSequenceItem(this, schema.owners, new User.Ref(model, user._id));
	}
	public void removeOwner(WikiventsModel model, User user) {
		if (owners.size()==1) // never remove the last organizer
			return;
		User.Ref ref = new User.Ref(model, user._id);
		model.groups.removeSequenceItem(this, schema.owners, ref);
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
