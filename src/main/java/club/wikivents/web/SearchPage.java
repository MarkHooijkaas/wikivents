package club.wikivents.web;

import java.util.ArrayList;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.Event;

public class SearchPage extends WikiventsPage {
	public SearchPage(WikiventsSite site) { super(site); }


	public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		TemplateData context = call.createTemplateData();
		String text = call.request.getParameter("text");
		context.add("text", text);
		if (text!=null)
			text=text.toLowerCase();
		ArrayList<Event> events=new ArrayList<Event>() ;
		for (Event e: call.model.allEvents) {
			if ((e.description+e.title+e.city+e.location).toLowerCase().indexOf(text)>0) {
				events.add(e);
			}
		}
		context.add("events", events);
		call.output(call.getTheme().search.toString(context));
	}

}