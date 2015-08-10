package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.Struct;

public class Page extends CrudObject implements AccessChecker<User> {
	public final String name;
	public final String content;
	public final ImmutableSequence<User.Ref> owners;
	
	public Page(WikiventsModel model, Struct data) {
		super(model.pages, data);
		this.name=schema.name.getString(data);
		this.content=schema.content.getString(data);
		this.owners=schema.owners.getSequence(model, data);
	}
	//@Override public String getUniqueSortingKey() { return ""+date+"|"+_id; }
	
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudSchema<Page> {
		private Schema() { super(Page.class); }
		public final IdField _id = new IdField();
		public final StringField name = new StringField("name"); 
		public final StringField content = new StringField("content"); 
		public final SequenceField<User.Ref> owners = new SequenceField<User.Ref>(User.Ref.type,"owners");
		//public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.schema,"comments"); 
	}


	
	@Override public boolean mayBeChangedBy(User user) { return user!=null && (user.isAdmin || hasOwner(user)); }
	@Override public boolean mayBeViewedBy(User user) { return true; }
	
//	public void addComment(WikiventsModel model, User user, String text) {
//		model.events.addSequenceItem(this, schema.comments, new Comment(user,text));
//	}
	public boolean hasOwner(User user) {
		for (User.Ref r: owners) {
			if (r._id.equals(user._id)) 
				return true;
		}
		return false;
	}

	public void addOwner(WikiventsModel model, User user) {
		if (hasOwner(user))
			return;
		model.pages.addSequenceItem(this, schema.owners, new User.Ref(model, user._id));
	}
}
