package club.wikivents.model;

import org.kisst.item4j.HasName;

public class GroupCommands {
	public static abstract class Command extends WikiventsCommands.Command<Group> {
		public Command(Group record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOwner(user); }
	}
	
	public static class ChangeFieldCommand extends WikiventsCommands.ChangeFieldCommand<Group> {
		public ChangeFieldCommand(Group record, HasName field, String value) {
			super(record, field.getName(), value);
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOwner(user);}
	}
}
