package org.kisst.crud4j;

import org.kisst.http4j.HttpBasicPage;
import org.kisst.http4j.HttpForm;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.HttpUserCall;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class HttpCrudHandler<T extends CrudObject> extends HttpBasicPage<HttpUserCall>{
	private final HttpForm form;
	protected final CrudTable<T> table;
			
	public HttpCrudHandler(CrudTable<T> table, HttpForm form) {
		this.form=form;
		this.table=table;
	}

	@Override public void handle(HttpUserCall call, String subPath) { new Call(call,subPath).handle(subPath);}
	

	public class Call extends HttpUserCall {
		public final T record;
		public Call(HttpUserCall call, String subPath) { 
			super(call);
			if (subPath==null || subPath.equals("") || subPath.equals("new"))
				this.record=null;
			else if (subPath.startsWith("edit/")) {
				this.record=table.read(subPath.substring(5));
				checkAuthorized();	
			}
			else
				this.record=table.read(subPath);
		}
		@Override public void handleGet(String subPath) {
			if (subPath==null || subPath.equals("") || subPath.equals("new"))
				showFormNewRecord();
			else if ( subPath.startsWith("edit/"))
				showFormExistingRecord(subPath.substring(5));
			else
				showRecord(subPath);
		}
		@Override public void handlePost(String subPath) {
			ensureUser();
			if (subPath==null || subPath.equals("") || subPath.equals("new"))
				createNewRecord();
			else if ( subPath.startsWith("edit/"))
				updateExistingRecord(subPath.substring(5));
			else
				invalidPage();
		}
		protected void showFormNewRecord() {
			Struct input=new HttpRequestStruct(this);
			form.showEditPage(this, input);
		}
		protected void showFormExistingRecord(String subPath) {
			form.showEditPage(this, record);
		}

		protected void showRecord(String subPath) {
			form.showViewPage(this, record);
		}


		protected void createNewRecord() {
			Struct input=new HttpRequestStruct(this);
			HashStruct data = new HashStruct(input);
			//String userid = ensureUserId(call);
			//validateCreate(userid, data);
			T rec=table.createObject(data);

			table.create(rec);
			redirect(rec._id);
		}
		protected void updateExistingRecord(String subPath) {
			checkAuthorized();
			Struct input=new HttpRequestStruct(this);
			T oldRecord=record;
			T newRecord=table.createObject(new MultiStruct(input,oldRecord));
			//validateUpdate(userid, oldRecord, newRecord);
			System.out.println("Updating "+subPath+" old:"+oldRecord+" new:"+newRecord);
			table.update(oldRecord, newRecord);
			redirect("/event/"+newRecord._id);
		}
		private void checkAuthorized() {
			if (record==null)
				return;
			if (! record.mayBeChangedBy(userid))
				throw new UnauthorizedException("Not Authorized user "+userid+" for "+record);
		}
	}
	//protected void validateUpdate(String userid, T oldRecord, T newRecord) {}
	//protected void validateCreate(String userid, HashStruct data) {}
}
