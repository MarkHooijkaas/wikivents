package club.wikivents.web;

import java.util.ArrayList;

import org.kisst.props4j.Props;

import com.github.jknack.handlebars.Context.Builder;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class HomeServlet extends HandlebarServlet {

	private final WikiventsModel model;

	public HomeServlet(WikiventsModel model, Props props) { 
		super(model, props);
		this.model=model;
	}

	@Override protected Builder addContext(Builder root) {
		//root=root.combine("model", model);
		//root=root.combine("events", model.events);
		ArrayList<User> users=new ArrayList<User>();
		for (User u: model.users.findAll()) {
			System.out.println("\t"+u);
			users.add(u);
		}
		//root=root.combine("users", model.users);
		User mark=model.users.findAll().get(0);
		root=root.combine("user", mark);
		return root;
	}

	
}
