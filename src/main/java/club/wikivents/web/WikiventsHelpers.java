package club.wikivents.web;

import java.io.IOException;

import org.kisst.http4j.handlebar.UserHelpers;

import com.github.jknack.handlebars.Options;

import club.wikivents.model.Event;
import club.wikivents.model.User;

public class WikiventsHelpers extends UserHelpers<User> {

	public WikiventsHelpers() { super(User.class, "authenticatedUser");}
	
	public CharSequence ifIsAdmin(final Options options) throws IOException { 
		User u=getUserOrNull(options);
		if (u!=null && u.isAdmin)
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifAmGuest(Event e, final Options options) throws IOException { 
		if (e.hasGuest((User) getUserOrNull(options))) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifLikedByMe(Event e, final Options options) throws IOException { 
		if (e.isLikedBy((User) getUserOrNull(options))) 
			return options.fn();
		return options.inverse();
	}

}
