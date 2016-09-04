package club.wikivents.model;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.item4j.seq.TypedSequence;
import org.kisst.pko4j.PkoTable;

import com.github.jknack.handlebars.Handlebars;

public class TagRepository implements Iterable<Tag> {
	private final ConcurrentHashMap<String,Tag> map = new ConcurrentHashMap<>();
	private final WikiventsModel model;

	public TagRepository(WikiventsModel model) {
		this.model = model;
	}

	public ImmutableSequence<User> getUsers(String tag) { return model.userTags.records(tag); }
	public ImmutableSequence<Event> getEvents(String tag) { return model.eventTags.records(tag); }

	public void addTags(String tags) {
		for (String tag : tags.split(","))
			addTag(tag);
	}

	public void addTag(String tag) {
		tag=tag.trim().toLowerCase();
		if (map.get(tag)==null)
			map.put(tag, new Tag(this,tag));
	}

	public static Handlebars.SafeString tagLinks(String tags) {
		String sep = "";
		String result = "";
		for (String tag : tags.split(",")) {
			result = result + sep + "<a href=\"/tag/" + tag + "\">" + tag + "</a>";
			sep = ", ";
		}
		return new Handlebars.SafeString(result);
	}

	public Tag[] tagList(String tags) {
		String[] tagNames=tags.split(",");
		Tag[] result = new Tag[tagNames.length];
		int i=0;
		for (String tag : tagNames) {
			result[i]=map.get(tag);
			i++;
		}
		return result;
	}

	@Override public Iterator<Tag> iterator() { return map.values().iterator(); }

	public class UserListener implements PkoTable.ChangeHandler<User> {
		@Override public Class<User> getRecordClass() { return User.class;}
		@Override public boolean allow(PkoTable<User>.Change change) { return true;}
		@Override public void rollback(PkoTable<User>.Change change) {}
		@Override public void commit(PkoTable<User>.Change change) {
			User rec = change.newRecord;
			if (rec!=null) {
				if (rec.tags != null) {
					User oldRec = change.oldRecord;
					if (oldRec == null || !rec.tags.equals(oldRec.tags))
						addTags(change.newRecord.tags);
				}
			}
		}
	}
	public class EventListener implements PkoTable.ChangeHandler<Event> {
		@Override public Class<Event> getRecordClass() { return Event.class;}
		@Override public boolean allow(PkoTable<Event>.Change change) { return true;}
		@Override public void rollback(PkoTable<Event>.Change change) {}
		@Override public void commit(PkoTable<Event>.Change change) {
			Event rec = change.newRecord;
			if (rec!=null) {
				if (rec.tags != null) {
					Event oldRec = change.oldRecord;
					if (oldRec == null || !rec.tags.equals(oldRec.tags))
						addTags(change.newRecord.tags);
				}
			}
		}
	}
}




