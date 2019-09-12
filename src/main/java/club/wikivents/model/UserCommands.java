package club.wikivents.model;

import club.wikivents.web.WikiventsCall;
import org.kisst.item4j.HasName;
import org.kisst.pko4j.PkoObject;

public class UserCommands {
	public static abstract class Command extends WikiventsCommands.Command<User> {
		public Command(User record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record==user; }
	}
	
	public static class ChangeFieldCommand extends WikiventsCommands.ChangeFieldCommand<User> {
		public ChangeFieldCommand(User record, HasName field, String value) {
			super(record, field.getName(), value);
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

	public static class ChangePassword  extends WikiventsCommands.Command<User> {
		public final String newPassword;
		public ChangePassword(User record, String newPassword) {
			super(record);
			this.newPassword=newPassword;
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || user==record;}//|| record.fieldMayBeChangedBy(field, user);}
		@Override public User apply() {
			return record.changePassword(this.newPassword);
		}
	}
}

