package org.kisst.http4j.handlebar;

import org.kisst.http4j.HttpUserPage;

public abstract class HttpHandlebarPage extends HttpUserPage {
	private final HttpHandlebarSite site;
	
	public HttpHandlebarPage(HttpHandlebarSite site) {this.site=site;}
	

	public Template createTemplate(String name) { return new Template(name); }
	public Data createTemplateData() { return new Data(); }
	
	public class Template extends HttpHandlebarSite.CompiledTemplate { public Template(String name) { site.super(name); } } 
	public class Data extends HttpHandlebarSite.TemplateData { public Data() { site.super(); } } 

	
}
