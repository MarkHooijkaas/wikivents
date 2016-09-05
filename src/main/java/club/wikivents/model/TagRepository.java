package club.wikivents.model;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.pko4j.PkoTable;
import org.kisst.util.FileUtil;

import com.github.jknack.handlebars.Handlebars;

public class TagRepository implements Iterable<Tag> {
	private final ConcurrentHashMap<String,Tag> map = new ConcurrentHashMap<>();
	private final WikiventsModel model;

	public TagRepository(WikiventsModel model) {
		this.model = model;
		loadTags(new File("data/predefined-tags.dat"));
	}

	private void loadTags(File file) {
		if (! file.exists())
			return;
		String content=FileUtil.loadString(file);
		for (String line :content.split("\n")){
			line=line.trim().toLowerCase();
			if (line.length()>0) {
				String[] parts = line.split(":");
				Tag parent=map.get(parts[0]);
				if (parent==null) {
					parent = new Tag(this, parts[0]);
					map.put(parent.name,parent);
				}
				for (String tag : parts[1].split(",")) {
					tag=tag.trim();
					if (tag.length()>0)
						map.put(tag,new Tag(this, parent, tag));
				}
			}
		}
	}

	public ImmutableSequence<User> getUsers(String tag) { return model.userTags.records(tag); }
	public ImmutableSequence<Event> getEvents(String tag) { return model.eventTags.records(tag); }

	public void addTags(String tags) {
		for (String tag : tags.split(","))
			addTag(tag);
	}

	public void addTag(String tag) {
		tag=tag.trim().toLowerCase();
		if (tag.length()==0)
			return;
		if (map.get(tag)==null)
			map.put(tag, new Tag(this,tag));
	}

	public TreeSet<Tag> orderedByPath() {
		TreeSet<Tag> result = new TreeSet<>((Tag t1, Tag t2) -> t1.path().compareTo(t2.path()));
		for (Tag tag:map.values())
			result.add(tag);
		return result;
	}

	public TreeSet<Tag> top() { return children(null);}
	public TreeSet<Tag> children(Tag parent) {
		TreeSet<Tag> result = new TreeSet<>((Tag t1, Tag t2) -> t1.name.compareTo(t2.name));
		for (Tag tag:orderedByPath()) {
			System.out.println(tag.name+"->"+tag.path());
			if (tag.parent==parent)
				result.add(tag);
		}
		return result;
	}

	public Handlebars.SafeString ultree() {
		StringBuilder result = new StringBuilder();
		result.append("<ul>");
		for (Tag child : top())
			addUltree(result, "\t", child);
		result.append("</ul>");
		return new Handlebars.SafeString(result);
	}

	private void addUltree(StringBuilder result, String indent, Tag tag) {
		result.append(indent+"<li><a href=\"/tag/"+tag.name+"\">"+tag.name+"</a>");
		//result.append(", "+tag.getEvents().size()+" activiteiten");
		//result.append(", "+tag.getUsers().size()+" gebruikers");
		TreeSet<Tag> set = children(tag);
		if (set.size()>0){
			result.append("\n"+indent+"\t<ul>\n");
			for (Tag child: set)
				addUltree(result,indent+"\t\t",child);
			result.append(indent+"\t</ul>\n"+indent);
		}
		result.append("</li>\n");
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




