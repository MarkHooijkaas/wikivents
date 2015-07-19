package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;


public class WikiventsModel extends CrudModel implements Item.Factory {
	public final User.Table users=new User.Table(getStorage(User.class), this);
	public final Event.Table events=new Event.Table(getStorage(Event.class),this);
	
	public WikiventsModel(StructStorage ... tables){ super(tables); }

	@Override public <T> T construct(Class<?> cls, Struct data) {
		if (Event.class==cls)
			return cast(new Event(this, data));
		else if (User.class==cls)
			return cast(new User(data));
		else if (User.Table.Ref.class == cls)
			return cast(users.createRef(data.getString("_id")));
		else if (Event.Table.Ref.class == cls)
			return cast(events.createRef(data.getString("_id")));
		return basicFactory.construct(cls, data);
	}
	@Override public <T> T construct(Class<?> cls, String data) { return basicFactory.construct(cls, data);}

	public void close() {
		users.close();
		events.close();
	}
}
