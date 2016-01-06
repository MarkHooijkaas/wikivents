package club.wikivents.model;

import org.kisst.item4j.HasName;

public class UserCommands {
	public static abstract class Command extends WikiventsCommands.Command<User> {
		public Command(User record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record==user; }
	}
	
	public static class ChangeFieldCommand extends WikiventsCommands.ChangeFieldCommand<User> {
		public ChangeFieldCommand(User record, HasName field, String value) {
			super(record, field, value);
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || record==user;}
	}
}
