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

	@Override public void handle(HttpUserCall call, String subPath) { new Call(call).handle(subPath);}
	

	private class Call extends HttpUserCall {
		public Call(HttpUserCall call) { super(call); }
		@Override public void handleGet(String subPath) {
			if (subPath==null || subPath.equals("") || subPath.equals("new"))
				showFormNewRecord();
			else if ( subPath.startsWith("edit/"))
				showFormExistingRecord(subPath);
			else
				showRecord(subPath);
		}
		@Override public void handlePost(String subPath) {
			if (subPath==null || subPath.equals("") || subPath.equals("new"))
				createNewRecord();
			else 
				updateExistingRecord(subPath);
		}
		protected void showFormNewRecord() {
			Struct input=new HttpRequestStruct(this);
			form.showEditPage(this, input);
		}
		protected void showFormExistingRecord(String subPath) {
			T rec=table.read(subPath);
			ensureUser(rec._id);
			//if (! rec.mayBeChangedBy(call.userid))
			//	throw new HttpUserCall.UnauthorizedException("Not Authorized user "+user+" for "+rec);
			form.showEditPage(this, rec);
		}
		protected void showRecord(String subPath) {
			T rec = table.read(subPath);
			form.showViewPage(this, rec);
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
			Struct input=new HttpRequestStruct(this);
			if ( subPath.startsWith("edit/")) 
				subPath=subPath.substring(5);
			//String userid = ensureUserId(call);
			T oldRecord=table.read(subPath);
			//if (! oldRecord.mayBeChangedBy(userid))
			//	throw new UnauthorizedException("Not Authorized user "+userid+" for "+oldRecord);
			T newRecord=table.createObject(new MultiStruct(input,oldRecord));
			//validateUpdate(userid, oldRecord, newRecord);
			table.update(oldRecord, newRecord);
			redirect(newRecord._id);
		}
	}
	//protected void validateUpdate(String userid, T oldRecord, T newRecord) {}
	//protected void validateCreate(String userid, HashStruct data) {}

}
