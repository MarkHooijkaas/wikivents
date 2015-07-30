package club.wikivents.web;

import org.kisst.crud4j.HttpCrudForm;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.FormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;

public class EventForm extends HttpCrudForm<Event> {
	public class Form extends Data {
		public Form(HttpCall call, Struct record) { super(call, record); }
		public final Field organizer=new Field("organizer", call.userid);
		public final Field title = new Field("title");
		public final Field date= new Field("date");
		public final Field min = new Field("min");
		public final Field max = new Field("max");
		public final Field location = new Field("location");
		public final Field description= new Field("description");
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

	@Override public Event createObject(Struct input) { return table.createObject(input); }
}

