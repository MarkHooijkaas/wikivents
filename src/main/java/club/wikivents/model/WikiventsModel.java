package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.StorageOption;
import org.kisst.crud4j.StructStorage;
import org.kisst.item4j.Item;
import org.kisst.item4j.struct.Struct;


public class WikiventsModel extends CrudModel implements Item.Factory {
	public final StructStorage users = getStorage(User.class);
	public final StructStorage events= getStorage(Event.class);

	public WikiventsModel(StorageOption ... tables){ super(tables);	}

	@Override public <T> T construct(Class<?> cls, Struct data) {
		//System.out.println("creating "+ReflectionUtil.smartClassName(cls)+" from "+data);
		if (Event.class==cls)
			return cast(new Event(data));
		else if (User.class==cls)
			return cast(new User(data));
		else if (Event.Comment.class == cls)
			return cast(new Event.Comment(data));
		else if (Event.Guest.class == cls)
			return cast(new Event.Guest(data));
		System.out.println("using normal factory");
		return basicFactory.construct(cls, data);
	}
	@Override public <T> T construct(Class<?> cls, String data) { return basicFactory.construct(cls, data);}

	public void close() {
		users.close();
		events.close();
	}
	
	public User findUsername(String username) {
		for (Struct u: users.findAll()) { 
			if (u.getString(username).equals(username)) // || u.email.equals(username))
				return new User(u);
		}
		return null;
	}

}
