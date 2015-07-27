package club.wikivents.web;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.TemplateEngine.CompiledTemplate;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

public class TemplatePage extends WikiventsPage {
	public final CompiledTemplate template;
	private final DataEnricher[] enrichers;
	
	public TemplatePage(WikiventsSite site, String templateName, DataEnricher ... enrichers) {
		super(site);
		this.template = engine.compileTemplate(templateName);
		this.enrichers=enrichers;
	}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall,model);
		TemplateData data = call.createTemplateData();
		addTemplateData(data, httpcall, subPath);
		call.output(template,data);
	}

	protected  void addTemplateData(TemplateData data, HttpCall httpcall, String subPath) {
		for (DataEnricher e: enrichers)
			e.addTemplateData(data, httpcall, subPath);
	}

	@FunctionalInterface
	public interface DataEnricher {
		public void addTemplateData(TemplateData data, HttpCall httpcall, String subPath);
	}
}
