package club.wikivents.model;

import org.kisst.crud4j.CrudModel;
import org.kisst.crud4j.StructStorage;


public class WikiventsModel extends CrudModel {
	public final User.Table users=new User.Table(getStorage(User.class));
	public final Event.Table events=new Event.Table(getStorage(Event.class),this);
	
	public WikiventsModel(StructStorage ... tables){ super(tables); } 
}
