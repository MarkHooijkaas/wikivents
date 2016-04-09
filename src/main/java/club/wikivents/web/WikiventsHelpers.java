package club.wikivents.web;

import java.io.IOException;

import org.kisst.http4j.HttpCall;
import org.kisst.http4j.handlebar.UserHelpers;

import com.github.jknack.handlebars.Options;

import club.wikivents.model.CommonBase;
import club.wikivents.model.Event;
import club.wikivents.model.Group;
import club.wikivents.model.User;

public class WikiventsHelpers extends UserHelpers<User> {

	public WikiventsHelpers() { super(User.class, "authenticatedUser");}
	
	public CharSequence ifIsAdmin(final Options options) throws IOException { 
		User u=getUserOrNull(options);
		if (u!=null && u.isAdmin)
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifAdminMode(final Options options) throws IOException { 
		User u=getUserOrNull(options);
		HttpCall call = getCallOrNull(options);
		if (u!=null && u.isAdmin && call!=null && "true".equals(call.request.getParameter("admin-mode")))
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifAmMember(CommonBase<?> rec, final Options options) throws IOException { 
		if (rec.hasMember(getUserOrNull(options))) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifAmOwner(Object o, final Options options) throws IOException { 
		boolean owner=false;
		User callUser = (User) getUserOrNull(options);
		if ((o instanceof CommonBase) && ((CommonBase<?>) o).hasOwner(callUser)) 
			owner=true;
		if ((o instanceof User) && ((User) o).equals(callUser)) 
			owner=true;
		if ((o instanceof User.Ref) && ((User.Ref) o).get0().equals(callUser)) 
			owner=true;
		if (owner)
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifEventHasGroup(Event e, Group gr, final Options options) throws IOException { 
		if (e.hasGroup(gr)) 
			return options.fn();
		return options.inverse();
	}
	public CharSequence ifLikedByMe(Object obj, final Options options) throws IOException {
		boolean liked = false;
		if (obj instanceof Event)
			liked= ((Event) obj).isLikedBy((User) getUserOrNull(options));
		if (liked)
			return options.fn();
		return options.inverse();
	}

}
