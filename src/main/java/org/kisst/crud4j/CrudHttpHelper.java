package org.kisst.crud4j;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.HttpServer;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.HttpHandlebarSite;
import org.kisst.http4j.handlebar.HttpHandlebarSite.CompiledTemplate;
import org.kisst.http4j.handlebar.HttpHandlebarSite.TemplateData;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class CrudHttpHelper<T extends CrudObject> {
	private final CompiledTemplate show;
	private final GenericForm form;
	protected final CrudTable<T> table;
	private final String name;
			
	public CrudHttpHelper(HttpHandlebarSite site, CrudTable<T> table, GenericForm form) {
		show=site.new CompiledTemplate("generic/show");
		this.table=table;
		this.form = form;
		this.name=table.schema.getJavaClass().getSimpleName().toLowerCase();
	}
	//@Override public String getPath() { return name+"/*"; }

	public void handleGet(HttpCall call) {
		String path = call.path;
		if (path==null || path.equals("") || path.equals("new"))
			showFormNewRecord(call);
		else if ( path.startsWith("edit/"))
			showFormExistingRecord(call);
		else
			showRecord(call);
	}
	public void handlePost(HttpCall call) {
		if (call.path==null || call.path.equals("") || call.path.equals("new"))
			createNewRecord(call);
		else 
			updateExistingRecord(call);
	}
	protected void showFormNewRecord(HttpCall call) {
		TemplateData data = new TemplateData(call.request);
		Struct input=new HttpRequestStruct(call);
		form.outputEdit(data, input, call.response);
	}
	protected void showFormExistingRecord(HttpCall call) {
		TemplateData data = new TemplateData(call.request);
		String path=call.path.substring(5);
		T rec=table.read(path);
		String user = ensureUserId(call);
		if (! rec.mayBeChangedBy(user))
			throw new HttpServer.UnauthorizedException("Not Authorized user "+user+" for "+rec);
		data.add("record", rec);
		data.add(path, rec);
		form.outputEdit(data, rec, call.response);
	}
	protected void showRecord(HttpCall call) {
		TemplateData data = createTemplateData(call);
		String user = getUserId(call);
		T rec = table.read(call.path);
		data.add("record", rec);
		data.add(call.path, rec);
		data.add("fields", form.renderShow(rec));
		data.add("userMayChange", rec.mayBeChangedBy(user));
		call.output(show.toString(data));
	}
	
	private String ensureUserId(HttpCall call) {
		String userid=getUserId(call);
		if (userid==null)
			throw new RuntimeException("TODO");
		return userid;
	}
	private String getUserId(HttpCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateData createTemplateData(HttpCall call) { 
		TemplateData data = new TemplateData(call);
		data.add("form", form);
		data.add("page", this);
		return data;
	}

	protected void createNewRecord(HttpCall call) {
		Struct input=new HttpRequestStruct(call);
		HashStruct data = new HashStruct(input);
		String userid = ensureUserId(call);
		validateCreate(userid, data);
		T rec=table.createObject(data);
		
		table.create(rec);
		call.redirect("/"+name+"/"+rec._id);
	}
	protected void updateExistingRecord(HttpCall call) {
		Struct input=new HttpRequestStruct(call);
		String path=call.path;
		if ( path.startsWith("edit/")) 
			path=path.substring(5);
		String userid = ensureUserId(call);
		T oldRecord=table.read(path);
		if (! oldRecord.mayBeChangedBy(userid))
			throw new HttpServer.UnauthorizedException("Not Authorized user "+userid+" for "+oldRecord);
		T newRecord=table.createObject(new MultiStruct(input,oldRecord));
		validateUpdate(userid, oldRecord, newRecord);
		table.update(oldRecord, newRecord);
		call.redirect("/"+name+"/"+newRecord._id);
	}

	protected void validateUpdate(String userid, T oldRecord, T newRecord) {}
	protected void validateCreate(String userid, HashStruct data) {}

}
