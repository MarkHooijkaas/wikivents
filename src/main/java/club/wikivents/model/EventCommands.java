package club.wikivents.model;

import org.kisst.item4j.HasName;

public class EventCommands {
	public static abstract class Command extends WikiventsCommands.Command<Event> {
		public Command(Event record) { super(record); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOrganizer(user); }
	}
	
	public static class AddGuestCommand extends Command {
		public final User guest;
		public AddGuestCommand(Event event, User guest) { super(event); this.guest=guest; }
		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && ! record.hasGuest(guest);
		}
		@Override public Event apply() {
			return record.changeField(Event.schema.guests, record.guests.growTail(new Guest(guest)));
		}
	}

	public static class RemoveGuestCommand extends Command {
		public final User guest;
		public RemoveGuestCommand(Event event, User guest) { super(event); this.guest=guest; }
		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && record.hasGuest(guest);
		}
		@Override public Event apply() {
			Guest g = record.findGuest(guest._id);
			if (g!=null)
				return record.changeField(Event.schema.guests, record.guests.removeItem(g));
			return record;
		}
	}
	
	public static class ChangeFieldCommand extends WikiventsCommands.ChangeFieldCommand<Event> {
		public ChangeFieldCommand(Event record, HasName field, String value) {
			super(record, field, value);
		}
		@Override public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOrganizer(user);}
	}
}
