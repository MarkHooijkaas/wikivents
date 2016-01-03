package club.wikivents.model;

import org.kisst.crud4j.CrudObject;
import org.kisst.crud4j.CrudSchema;
import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.struct.Struct;

public class GroupData extends CrudObject {
	public static final Schema schema=new Schema();
	public static final class Schema extends CrudSchema<Group> {
		private Schema() { super(Group.class); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField description = new StringField("description"); 
		public final SequenceField<User.Ref> owners = new SequenceField<User.Ref>(User.Ref.type,"owners");
		public final SequenceField<User.Ref> members= new SequenceField<User.Ref>(User.Ref.type,"members"); 
		public final SequenceField<Comment> comments= new SequenceField<Comment>(Comment.schema,"comments");
	}

	public final WikiventsModel model;
	public final String title;
	public final String description;
	public final ImmutableSequence<User.Ref> owners;
	public final ImmutableSequence<User.Ref> members;
	public final ImmutableSequence<Comment> comments;

	public GroupData(WikiventsModel model, Struct data) {
		super(model.groups, data);
		this.model=model;
		this.title=schema.title.getString(data);
		this.description=schema.description.getString(data);
		this.owners=schema.owners.getSequenceOrEmpty(model, data);
		this.members=schema.members.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
		
	}


}
