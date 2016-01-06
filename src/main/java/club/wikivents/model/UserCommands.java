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

	public static class RemoveRecommendationCommand extends Command {
		public final String recommenderId;
		public RemoveRecommendationCommand(User record, String recommenderId) { super(record); this.recommenderId=recommenderId; }
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || user._id.equals(recommenderId); 	}
		@Override public User apply() {
			UserItem recommendation=record.findRecommendation(recommenderId);
			return record.changeField(User.schema.recommendations, record.recommendations.removeItem(recommendation));
		}
	}
}

