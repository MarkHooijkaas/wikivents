package club.wikivents.web;

import static club.wikivents.model.Event.schema;

import org.kisst.crud4j.HttpCrudForm;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;

public class EventForm extends HttpCrudForm<Event> {
	public class Form extends Data {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final Field organizer=new Field(schema.organizer, call.userid);
		public final Field title = new Field(schema.title);
		public final Field date= new Field(schema.date);
		public final Field min = new Field(schema.min);
		public final Field max = new Field(schema.max);
		public final Field location = new Field(schema.location);
		public final Field description= new Field(schema.description);
	}

	public EventForm(WikiventsSite site) {
		super(site.model.events, site.engine, "event");
	}

	@Override public FormData createFormData(HttpCall call, Struct struct) { return new Form(call,struct); }
	@Override public boolean isAuthorized(Struct oldRecord, HttpCall call) {
		if (((WikiventsCall)call).userid==null)
			return false;
		return ((WikiventsCall)call).userid.equals(oldRecord.getString("organizer" ,null));
	}
	
	/*
	@Override public void validateCreate(String userid, Struct data) {
		User.Table.Ref user=model.users.createRef(userid);
		if (user==null)
			throw new RuntimeException("no user");
		data.put(Event.schema.organizer.getName(),user);
	}
	*/
	//public void validateUpdate(String userid, Struct oldRecord, Struct newRecord) {}
	
	
	
	//@Override public void handle(WikiventsCall call, String subPath) { handler.handle(call, subPath); }
}

