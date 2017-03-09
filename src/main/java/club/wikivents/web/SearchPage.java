package club.wikivents.web;

import java.util.ArrayList;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;

import club.wikivents.model.Event;
import club.wikivents.model.Group;
import club.wikivents.model.User;

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
			if ((e.description+e.title+e.city+e.location+e.tags).toLowerCase().indexOf(text)>=0) {
				events.add(e);
			}
		}
		ArrayList<Group> themes=new ArrayList<Group>() ;
		for (Group g: call.model.groups) {
			if ((g.description+g.title).toLowerCase().indexOf(text)>=0) {
				themes.add(g);
			}
		}
		if (call.authenticated) {
			ArrayList<User> users=new ArrayList<User>() ;
			for (User u: call.model.users) {
				if ((u.description+u.username+u.city+u.tags).toLowerCase().indexOf(text)>=0) {
					users.add(u);
				}
			}		
			context.add("users", users);
		}
		context.add("events", events);
		context.add("groups", themes);
		call.output(call.getTheme().search.toString(context));
	}

}