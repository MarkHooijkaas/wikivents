package org.kisst.servlet4j;

public abstract class HttpHandlebarPage extends UserPage {
	private final HttpHandlebarSite site;
	
	public HttpHandlebarPage(HttpHandlebarSite site) {this.site=site;}
	

	public Template createTemplate(String name) { return new Template(name); }
	public Data createTemplateData() { return new Data(); }
	
	public class Template extends HttpHandlebarSite.CompiledTemplate { public Template(String name) { site.super(name); } } 
	public class Data extends HttpHandlebarSite.TemplateData { public Data() { site.super(); } } 

	
}
