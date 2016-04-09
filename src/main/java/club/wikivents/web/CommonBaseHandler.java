package club.wikivents.web;

import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.index.UniqueIndex;
import org.kisst.util.CallInfo;

import club.wikivents.model.Comment;
import club.wikivents.model.CommonBase;
import club.wikivents.model.Event;
import club.wikivents.model.User;

public class CommonBaseHandler<T extends CommonBase<T> & HasUrl> extends WikiventsActionHandler<T> {
	public final CommonBase.Schema<T> schema;

	public CommonBaseHandler(WikiventsSite site, PkoTable<T> table, UniqueIndex<T> index, CommonBase.Schema<T> schema) { 
		super(site, table, index); 
		this.schema=schema;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@NeedsNoAuthorization
	public void handleAddComment(WikiventsCall call, T rec) {
		String text=call.request.getParameter("comment");
		table.update(rec, rec.addSequenceItem(schema.comments, new Comment(call.user,text)));
	}
	
	public void handleRemoveComment(WikiventsCall call, T rec) {
		String commentId=call.request.getParameter("commentId");
		if (rec==null || commentId==null)
			return;
		Comment com=rec.findComment(commentId);
		table.update(rec, rec.removeSequenceItem(schema.comments, com));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@NeedsNoAuthorization
	public void handleAddMember(WikiventsCall call, T rec) {
		if (!rec.hasMember(call.user))
			table.update(rec, rec.addSequenceItem(schema.members, call.user.getRef()));
	}
	
	@NeedsNoAuthorization
	public void handleRemoveMember(WikiventsCall call,T rec) {
		String userId=call.request.getParameter("userId");
		if (call.user._id.equals(userId) || call.user.isAdmin)
			table.update(rec, rec.removeSequenceItem(schema.members, User.Ref.of(model, userId)));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void handleAddOwner(WikiventsCall call, T rec) {
		String username=call.request.getParameter("newOwner");
		User newOwner=model.usernameIndex.get(username);
		if (newOwner==null)
			call.sendError(500, "no owner");
		else if (! rec.hasOwner(newOwner))
			table.update(rec, rec.addSequenceItem(schema.owners, newOwner.getRef()));
	}
	public void handleRemoveOwner(WikiventsCall call, T rec) {
		String id=call.request.getParameter("userId");
		User user=model.users.read(id);
		if (user==null)
			call.sendError(500, "no owner");
		else if (rec.owners.size()>1) // never remove the last organizer
			table.update(rec, rec.removeSequenceItem(schema.owners, user.getRef()));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@NeedsNoAuthorization
	public void handleAddLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		if (! event.isLikedBy(call.user))
			model.events.update(event, event.addSequenceItem(Event.schema.likes, call.user.getRef()));
	}

	@NeedsNoAuthorization
	public void handleRemoveLike(WikiventsCall call) {
		String id=call.request.getParameter("eventId");
		Event event=model.events.read(id);
		if (event!=null)
			CallInfo.instance.get().data=event.title;
		User.Ref ref = call.user.getRef();
		model.events.update(event, event.removeSequenceItem(Event.schema.likes, ref));
	}

}

