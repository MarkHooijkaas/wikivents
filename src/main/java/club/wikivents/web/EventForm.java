package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;

import club.wikivents.model.Event;

public class EventForm extends WikiventsThing {
	public EventForm(WikiventsSite site) { super(site);	}

	private final CompiledTemplate template=engine.compileTemplate("event/edit");

	public class Form extends HttpFormData {
		public Form(HttpCall call) { super(call, template); }
		public final InputField organizer=new InputField("organizer", call.userid);
		public final InputField title = new InputField("title");
		public final InputField date= new InputField("date");
		public final InputField min = new InputField("min");
		public final InputField max = new InputField("max");
		public final InputField location = new InputField("location");
		public final InputField description= new InputField("description");
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
			formdata.showForm();
		else {
			if (formdata.isValid()) {
				Event newRecord=oldRecord.modified(model, formdata.record);
				System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
				model.events.update(oldRecord, newRecord);
			}
			formdata.handle();
		}
	}

}

