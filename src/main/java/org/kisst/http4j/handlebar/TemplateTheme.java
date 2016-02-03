package org.kisst.http4j.handlebar;

import java.util.HashMap;

import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;

public class TemplateTheme {
	private final HashMap<String, CompiledTemplate> map=new HashMap<String, CompiledTemplate>();
	public final TemplateEngine engine;
	private final String prefix;
	
	public TemplateTheme(TemplateEngine engine) { this.engine=engine; this.prefix="";}
	//public TemplateTheme(TemplateEngine engine, String prefix) { this.engine=engine; this.prefix=prefix; }
	
	public CompiledTemplate getTemplate(String name) { 
		CompiledTemplate result = map.get(name);
		if (result==null)
			throw new IllegalArgumentException("No template with name "+name+" in dir "+engine);
		return result;
	}

	//public TemplateTheme add(String name, CompiledTemplate templ) { map.put(name, templ); return this;}
	//public TemplateTheme add(String name) { map.put(name, engine.compileTemplate(prefix+name)); return this;}
	public CompiledTemplate template(String name) {
		CompiledTemplate templ = engine.compileTemplate(prefix+name);
		map.put(name, templ);
		return templ;
	}

}
