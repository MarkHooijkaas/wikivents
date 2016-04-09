package club.wikivents.model;

import org.kisst.item4j.HasName;


public class CommonBaseCommands<T extends CommonBase<T>> {
	public static abstract class Command<T extends CommonBase<T>> extends WikiventsCommands.Command<T> {
		public Command(T record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOwner(user); }
	}

	
	public static class ChangeFieldCommand<T extends CommonBase<T>> extends WikiventsCommands.ChangeFieldCommand<T> {
		public ChangeFieldCommand(T record, HasName field, String value) {
			super(record, field.getName(), value);
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOwner(user);}
	}
	
	public static class AddMemberCommand<T extends CommonBase<T>> extends Command<T> {
		public final User member;
		public AddMemberCommand(T record, User member) { super(record); this.member=member; }
		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && ! record.hasMember(member);
		}
		@Override public T apply() {
			return record.changeField(CommonBase.commonSchema.members, record.members.growTail(member.getRef()));
		}
	}

	public static class RemoveMemberCommand<T extends CommonBase<T>> extends Command<T> {
		public final User member;
		public RemoveMemberCommand(T record, User member) { super(record); this.member=member; }
		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && record.hasMember(member);
		}
		@Override public T apply() {
			if (member!=null)
				return record.changeField(CommonBase.commonSchema.members, record.members.removeItem(member.getRef()));
			return record;
		}
	}
}
