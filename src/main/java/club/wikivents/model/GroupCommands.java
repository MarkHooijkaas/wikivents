package club.wikivents.model;

public class GroupCommands extends CommonBaseCommands<Group>{
	public static abstract class Command extends WikiventsCommands.Command<Group> {
		public Command(Group record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOwner(user); }
	}
}
