package org.kisst.crud4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kisst.http4j.HttpRequestStruct;
import org.kisst.http4j.HttpServer;
import org.kisst.http4j.handlebar.GenericForm;
import org.kisst.http4j.handlebar.HttpHandlebarPage;
import org.kisst.http4j.handlebar.HttpHandlebarSite;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class CrudPage<T extends CrudObject> extends HttpHandlebarPage  {
	private final Template show;
	private final GenericForm form;
	protected final CrudTable<T> table;
	private final String name;
			
	public CrudPage(HttpHandlebarSite site, CrudTable<T> table, GenericForm form) {
		super(site);
		show=createTemplate("generic/show");
		this.table=table;
		this.form = form;
		this.name=table.schema.getJavaClass().getSimpleName().toLowerCase();
	}
	@Override public String getPath() { return name+"/*"; }

	@Override public void handleGet(String path, HttpServletRequest request, HttpServletResponse response) {
		if (path==null || path.equals("") || path.equals("new"))
			showFormNewRecord(path,request, response);
		else if ( path.startsWith("edit/"))
			showFormExistingRecord(path, request, response);
		else
			showRecord(path, request, response);
	}
	@Override public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {
		if (path==null || path.equals("") || path.equals("new"))
			createNewRecord(path,request, response);
		else 
			updateExistingRecord(path, request, response);
	}
	protected void showFormNewRecord(String path, HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		Struct input=new HttpRequestStruct(request);
		form.outputEdit(data, input, response);
	}
	protected void showFormExistingRecord(String path, HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		path=path.substring(5);
		T rec=table.read(path);
		String user = ensureUserId(request,response);
		if (! rec.mayBeChangedBy(user))
			throw new HttpServer.UnauthorizedException("Not Authorized user "+user+" for "+rec);
		data.add("record", rec);
		data.add(path, rec);
		form.outputEdit(data, rec, response);
	}
	protected void showRecord(String path, HttpServletRequest request, HttpServletResponse response) {
		Data data = createTemplateData(request);
		String user = getUserId(request);
		T rec = table.read(path);
		data.add("record", rec);
		data.add(path, rec);
		data.add("fields", form.renderShow(rec));
		data.add("userMayChange", rec.mayBeChangedBy(user));
		show.output(data, response);
	}
	public Data createTemplateData(HttpServletRequest request) { 
		Data data = new Data();
		data.add("form", form);
		data.add("page", this);
		return data;
	}

	protected void createNewRecord(String path, HttpServletRequest request, HttpServletResponse response) {
		Struct input=new HttpRequestStruct(request);
		HashStruct data = new HashStruct(input);
		String userid = ensureUserId(request,response);
		validateCreate(userid, data);
		T rec=table.createObject(data);
		
		table.create(rec);
		redirect(response,"/"+name+"/"+rec._id);
	}
	protected void updateExistingRecord(String path, HttpServletRequest request, HttpServletResponse response) {
		Struct input=new HttpRequestStruct(request);
		if ( path.startsWith("edit/")) 
			path=path.substring(5);
		String userid = ensureUserId(request,response);
		T oldRecord=table.read(path);
		if (! oldRecord.mayBeChangedBy(userid))
			throw new HttpServer.UnauthorizedException("Not Authorized user "+userid+" for "+oldRecord);
		T newRecord=table.createObject(new MultiStruct(input,oldRecord));
		validateUpdate(userid, oldRecord, newRecord);
		table.update(oldRecord, newRecord);
		redirect(response,"/"+name+"/"+newRecord._id);
	}

	protected void validateUpdate(String userid, T oldRecord, T newRecord) {}
	protected void validateCreate(String userid, HashStruct data) {}

}
