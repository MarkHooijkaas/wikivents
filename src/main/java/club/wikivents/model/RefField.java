package club.wikivents.model;

import org.kisst.item4j.Schema;

public  class RefField extends Schema.BasicField<User.Ref> {
	public RefField(String name) { super(User.Ref.type, name); }
}
