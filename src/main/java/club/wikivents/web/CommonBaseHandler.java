package club.wikivents.web;

import org.kisst.pko4j.PkoTable;
import org.kisst.pko4j.index.UniqueIndex;
import org.kisst.util.CallInfo;

import club.wikivents.model.Comment;
import club.wikivents.model.CommonBase;
import club.wikivents.model.Event;
import club.wikivents.model.User;
import club.wikivents.model.CommonBaseCommands.AddMemberCommand;
import club.wikivents.model.CommonBaseCommands.RemoveMemberCommand;

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
	
	@NeedsNoAuthorization
	public void handleRemoveComment(WikiventsCall call, T rec) {
		String commentId=call.request.getParameter("commentId");
		if (rec==null || commentId==null)
			return;
		Comment com=rec.findComment(commentId);
		if (rec.mayBeChangedBy(call.user) || com.user.getKey().equals(call.user._id))
			table.update(rec, rec.removeSequenceItem(schema.comments, com));
		else
			logger.warn("User:"+call.user.username+" not allowed to remove comment "+com);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public AddMemberCommand<T> createAddMemberCommand(WikiventsCall call, T rec) {
		return new AddMemberCommand<>(rec, call.user);
	}
	public RemoveMemberCommand<T> createRemoveMemberCommand(WikiventsCall call, T rec) {
		String memberId=call.request.getParameter("member");
		User member = model.users.read(memberId);
		return new RemoveMemberCommand<>(rec, member);
	}

	
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
		else if (rec.owners.size()>1) // never remove the last owner
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

	/*
	public void handleAddPoll(WikiventsCall call, Event event) {
		table.update(event,
			event.addSequenceItem(schema.polls, event.new Poll(new HttpRequestStruct(call)))
		);
	}
	*/
}

