package club.wikivents.model;

public class EventCommands {
	public static abstract class EventCommand extends WikiventsModel.Command<Event> {
		public EventCommand(WikiventsModel model, Event event) { super(model,event); }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || record.hasOrganizer(user); }
	}
	
	public static class AddGuestCommand extends EventCommand {
		public final User guest;
		public AddGuestCommand(WikiventsModel model, Event event, User guest) { super(model, event); this.guest=guest; }
		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && ! record.hasGuest(guest);
		}
		@Override public Event apply() {
			return record.changeField(Event.schema.guests, record.guests.growTail(new Guest(model, guest)));
		}
	}

	public static class RemoveGuestCommand extends EventCommand {
		public final User guest;
		public RemoveGuestCommand(WikiventsModel model, Event event, User guest) { super(model, event); this.guest=guest; }
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
}
