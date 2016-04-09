package club.wikivents.model;

import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.PkoModel;
import org.kisst.pko4j.PkoRef;

public abstract class GroupData extends CommonBase<Group> {
	@Override public Iterable<String> fieldNames() { return schema.fieldNames(); }
	public static final Schema schema=new Schema();
	public static final class Schema extends CommonBase.Schema<Group> {
		private Schema() { super(Group.class); }
	}

	public GroupData(WikiventsModel model, Struct data) {
		super(schema, model, model.groups, data);
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
