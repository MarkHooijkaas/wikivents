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
	private final CrudTable<T> table;
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
		Data data = createTemplateData(request);
		Struct input=new HttpRequestStruct(request);
		if (path==null || path.equals("") || path.equals("new")) {
			// GET a form to create new Event
			form.outputEdit(data, input, response);
		}
		if ( path.startsWith("edit/")) {
			path=path.substring(5);
			T rec=table.read(path);
			String user = ensureUserId(request,response);
			if (! rec.mayBeChangedBy(user))
				throw new HttpServer.UnauthorizedException("Not Authorized user "+user+" for "+rec);
			data.add("record", rec);
			data.add(path, rec);
			form.outputEdit(data, rec, response);
		}
		else {
			String user = getUserId(request);
			// GET the data for a existing record
			T rec = table.read(path);
			//System.out.println("Showing "+rec);
			data.add("record", rec);
			data.add(path, rec);
			data.add("fields", form.renderShow(rec));
			data.add("userMayChange", rec.mayBeChangedBy(user));
			show.output(data, response);
		}
	}
	public Data createTemplateData(HttpServletRequest request) { 
		Data data = new Data();
		data.add("form", form);
		data.add("page", this);
		return data;
	}
	
	@Override public void handlePost(String path, HttpServletRequest request, HttpServletResponse response) {
		Struct input=new HttpRequestStruct(request);
		if (path==null || path.equals("") || path.equals("new")) {
			HashStruct data = new HashStruct(input);
			//String id = ensureUserId(request,response);
			// TODO: data.put(schema.organizer.getName(),id);
			T rec=table.createObject(data);
			table.create(rec);
		}
		else { 
			if ( path.startsWith("edit/")) 
				path=path.substring(5);
			T oldRecord=table.read(path);
			String user = ensureUserId(request,response);
			T newRecord=table.createObject(new MultiStruct(input,oldRecord));
			
			if (! oldRecord.mayBeChangedBy(user))
				throw new HttpServer.UnauthorizedException("Not Authorized user "+user+" for "+oldRecord);
			table.update(oldRecord, newRecord);
			redirect(response,"/"+name+"/"+newRecord._id);
		}
	}

}
