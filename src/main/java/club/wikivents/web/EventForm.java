package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.item4j.struct.Struct;

import club.wikivents.model.Event;

public class EventForm extends WikiventsThing {
	public EventForm(WikiventsSite site) { super(site);	}

	public class Form extends HttpFormData {
		public Form(WikiventsCall call, Struct data) { super(call, data, call.getTheme().eventEdit); }

		public Form(WikiventsCall call) { super(call, call.getTheme().eventEdit); }
		public final InputField organizer=new InputField("organizer", call.userid);
		public final InputField title = new InputField(Event.schema.title);
		public final InputField imageUrl = new InputField(Event.schema.imageUrl);
		public final InputField date= new InputField(Event.schema.date);
		public final InputField time = new InputField(Event.schema.time);
		public final InputField min = new InputField(Event.schema.min);
		public final InputField max = new InputField(Event.schema.max);
		public final InputField guestsAllowed = new InputField(Event.schema.guestsAllowed);
		public final InputField backupGuestsAllowed= new InputField(Event.schema.backupGuestsAllowed);
		public final InputField location = new InputField(Event.schema.location);
		public final InputField guestInfo = new InputField(Event.schema.guestInfo);
		public final InputField description= new InputField(Event.schema.description);
	}

	public void handleCreate(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		Form formdata = new Form(call);
		if (call.isGet()) 
			formdata.showForm();
		else {
			if (formdata.isValid()) {
				Event evt = new Event(model, formdata.record);
				model.events.create(evt);
				evt.addOrganizer(model, call.user);
			}
			formdata.handle();
		}
	}
	
	public void handleEdit(HttpCall httpcall, String subPath) {
		WikiventsCall call = WikiventsCall.of(httpcall, model);
		Event oldRecord = model.events.read(subPath);
		Form formdata = new Form(call);
		
		if (! oldRecord.mayBeChangedBy(call.user))
			formdata.error("Not authorized");
		
		if (call.isGet())
			new Form(call,oldRecord).showForm();
		else {
			if (formdata.isValid())
				model.events.updateFields(oldRecord, formdata.record);
			formdata.handle();
		}
	}

}

