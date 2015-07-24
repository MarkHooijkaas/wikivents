package org.kisst.crud4j;

import org.kisst.http4j.HttpCallDispatcher;
import org.kisst.http4j.HttpForm;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.HttpUserCall;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class HttpCrudDispatcher<T extends CrudObject> extends HttpCallDispatcher<HttpUserCall>{
	private final HttpForm form;
	protected final CrudTable<T> table;

	public HttpCrudDispatcher(CrudTable<T> table, HttpForm form) {
		super(null);
		this.form=form;
		this.table=table;
		addHandler("new",  (HttpUserCall call, String subPath) -> new NewCall(call).handle(subPath));
		addHandler("show", (HttpUserCall call, String subPath) -> new ShowCall(call,subPath).handle(subPath));
		addHandler("edit", (HttpUserCall call, String subPath) -> new EditCall(call,subPath).handle(subPath));
	}
	public T read(String subPath) {
		System.out.println("Searching "+subPath);
		while (subPath.endsWith("?"))
			subPath=subPath.substring(0,subPath.length()-1);
		return table.read(subPath);
	}
	

	private class NewCall extends HttpUserCall {
		public NewCall(HttpUserCall call) { super(call); }
		@Override public void handleGet(String subPath) { form.showEditPage(this, new HttpRequestStruct(this)); }
		@Override public void handlePost(String subPath) {
			Struct input=new HttpRequestStruct(this);
			HashStruct data = new HashStruct(input);
			//validateCreate(userid, data);
			T rec=table.createObject(data);
			table.create(rec);
			redirect("../show/"+rec._id);
		}
	}

	private class ShowCall extends HttpUserCall {
		private final T record;
		public ShowCall(HttpUserCall call, String subPath) { 
			super(call);
			this.record=read(subPath);
		}
		@Override public void handleGet(String subPath) { form.showViewPage(this, record); }
		@Override public void handlePost(String subPath) { invalidPage();	}
	}

	private class EditCall extends HttpUserCall {
		private final T record;
		public EditCall(HttpUserCall call, String subPath) { 
			super(call);
			this.record=read(subPath);
			checkAuthorized();	
		}
		@Override public void handleGet(String subPath) { form.showEditPage(this, record); }
		@Override public void handlePost(String subPath) {
			Struct input=new HttpRequestStruct(this);
			T oldRecord=record;
			T newRecord=table.createObject(new MultiStruct(input,oldRecord));
			//validateUpdate(userid, oldRecord, newRecord);
			System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
			table.update(oldRecord, newRecord);
			redirect("../show/"+newRecord._id);
		}
		private void checkAuthorized() {
			if (record==null)
				return;
			if (! record.mayBeChangedBy(userid))
				throw new UnauthorizedException("Not Authorized user "+userid+" for "+record);
		}
	}

}
