package club.wikivents.model;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoRef;
import org.kisst.pko4j.PkoSchema;

public class GroupData extends BasicPkoObject<WikiventsModel, Group> {
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	public static final Schema schema=new Schema();
	public static final class Schema extends PkoSchema<Group> {
		private Schema() { super(Group.class); }
		public IdField getKeyField() { return _id; }
		public final IdField _id = new IdField();
		public final IntField _crudObjectVersion = new IntField("_crudObjectVersion");
		public final StringField title = new StringField("title"); 
		public final StringField description = new StringField("description"); 
		public final SequenceField<User.Ref> owners = new SequenceField<>(User.Ref.class,"owners");
		public final SequenceField<User.Ref> members= new SequenceField<>(User.Ref.class,"members"); 
		public final SequenceField<Comment> comments= new SequenceField<>(Comment.class,"comments");
	}
	
	public final String title;
	public final String description;
	public final ImmutableSequence<User.Ref> owners;
	public final ImmutableSequence<User.Ref> members;
	public final ImmutableSequence<Comment> comments;

	public GroupData(WikiventsModel model, Struct data) {
		super(model, model.groups, data);
		this.title=schema.title.getString(data);
		this.description=schema.description.getString(data);
		this.owners=schema.owners.getSequenceOrEmpty(model, data);
		this.members=schema.members.getSequenceOrEmpty(model, data);
		this.comments=schema.comments.getSequenceOrEmpty(model, data);
		
	}

	@Override public Ref getRef() { return Ref.of(model,_id); }
	public static class Ref extends PkoRef<Group> implements PkoModel.MyObject {
		static public Ref of(WikiventsModel model, String key) { return key==null ? null : new Ref(model, key); }
		public static class Field extends Schema.BasicField<Group.Ref> {
			public Field(String name) { super(Group.Ref.class, name); }
			public Ref getRef(WikiventsModel model, Struct data) { return of(model, Item.asString(data.getDirectFieldValue(name)));}
		}
		private Ref(WikiventsModel model, String _id) { super(model.groups, _id); }
	}
	@Override public String getName() { return title; }
}
