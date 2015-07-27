package club.wikivents.web;

import static club.wikivents.model.Event.schema;

import org.kisst.crud4j.HttpCrudDispatcher;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;

public class EventCrudPage extends HttpCrudDispatcher<Event> {
	public class Form extends HttpForm {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final StringField title = new StringField(schema.title);
		public final DateField date= new DateField(schema.date);
		public final StringField min = new StringField(schema.min);
		public final StringField max = new StringField(schema.max);
		public final StringField location = new StringField(schema.location);
		public final StringField description= new StringField(schema.description);
	}

	public EventCrudPage(WikiventsSite site) {
		super(site.model.events, site.engine, "event");
	}

	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (((WikiventsCall)call).userid==null)
			return false;
		return ((WikiventsCall)call).userid.equals(oldRecord.getString("organizer" ,null));
	}
	
	/*public void validateCreate(String userid, HashStruct data) {
		User.Table.Ref user=model.users.createRef(userid);
		if (user==null)
			throw new RuntimeException("no user");
		data.put(Event.schema.organizer.getName(),user);
	}*/
	//public void validateUpdate(String userid, Struct oldRecord, Struct newRecord) {}
	
	
	
	//@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
}

