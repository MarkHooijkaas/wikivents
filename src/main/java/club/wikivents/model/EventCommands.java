package club.wikivents.model;

import org.kisst.pko4j.PkoCommand;

public class EventCommands {
	public abstract class EventCommand implements PkoCommand<Event> {
		public final WikiventsModel model;
		public final Event event;
		public EventCommand(WikiventsModel model, Event event) { this.model=model; this.event=event; }
		public boolean mayBeDoneBy(User user) { return user.isAdmin || event.hasOrganizer(user); }
		@Override public void otherActions(boolean rerun) {}
		@Override public Event target() {return event;}
	}
	
	public class AddGuestCommand extends EventCommand {
		public final User guest;
		public AddGuestCommand(WikiventsModel model, Event event, User guest) { super(model, event); this.guest=guest; }

		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && ! event.hasGuest(guest);
		}
		@Override public Event apply() {
			return (Event) event.changeField(Event.schema.guests, event.guests.growTail(new Guest(model, guest)));
		}
	}

	public class RemoveGuestCommand extends EventCommand {
		public final User guest;
		public RemoveGuestCommand(WikiventsModel model, Event event, User guest) { super(model, event); this.guest=guest; }

		@Override public boolean mayBeDoneBy(User user) { 
			return user.mayParticipate() && event.hasGuest(guest);
		}
		@Override public Event apply() {
			Guest g = event.findGuest(guest._id);
			if (g!=null)
				return (Event) event.changeField(Event.schema.guests, event.guests.removeItem(g));
			return event;
		}
	}
}
