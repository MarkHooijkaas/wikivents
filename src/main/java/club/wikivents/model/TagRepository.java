package club.wikivents.model;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.kisst.item4j.ImmutableSequence;
import org.kisst.pko4j.PkoTable;
import org.kisst.util.FileUtil;

import com.github.jknack.handlebars.Handlebars;

public class TagRepository implements Iterable<Tag> {
	private final ConcurrentHashMap<String,Tag> map = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String,String> related = new ConcurrentHashMap<>();
	private final WikiventsModel model;

	public TagRepository(WikiventsModel model) {
		this.model = model;
		preloadTags(new File("data/predefined-tags.dat"));
	}

	private void preloadTags(File file) {
		if (! file.exists())
			return;
		String content=FileUtil.loadString(file);
		for (String line :content.split("\n")){
			line=line.trim().toLowerCase();
			if (line.length()>0)
				addTag(line);
		}
	}

	public ImmutableSequence<User> getUsers(String tag) { return model.userTags.records(tag); }
	public ImmutableSequence<Wikivent> getEvents(String tag) { return model.eventTags.records(tag); }

	public void addTags(String tags) {
		for (String tag : tags.split(","))
			addTag(tag);
	}

	public Tag addTag(String name) {
		name=name.trim().toLowerCase();
		if (name.length()==0)
			return null;
		int pos=name.lastIndexOf(':');
		String typeName=null;
		if (pos>0) {
			typeName = name.substring(0, pos);
			name = name.substring(pos + 1).trim();
		}
		Tag tag = findTag(name);
		if (typeName!=null){
			addType(typeName, tag);
		}
		return tag;
	}

	private void addType(String typeName, Tag tag) {
		int pos=typeName.indexOf(':');
		if (pos>0)
			addType(typeName.substring(0,pos),tag);
		Tag type=findTag(typeName);
		tag.addParent(typeName);
		type.addChild(tag.name);
	}

	public Tag getTag(String name) {
		int pos=name.lastIndexOf(':');
		if (pos>0)
			name=name.substring(pos+1);
		return map.get(name.trim());
	}
	private Tag findTag(String name) {
		int pos=name.lastIndexOf(':');
		if (pos>0)
			name=name.substring(pos+1);
		Tag tag = map.get(name.trim());
		if (tag == null) {
			tag = new Tag(this, name);
			map.put(name, tag);
		}
		return tag;
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
		while (tags.startsWith(","))
			tags=tags.substring(1);
		while (tags.endsWith(","))
			tags=tags.substring(0,tags.length()-1);
		if (tags.length()==0)
			return new Tag[0];
		String[] tagNames=tags.split(",");
		Tag[] result = new Tag[tagNames.length];
		int i=0;
		for (String tag : tagNames) {
			result[i]=getTag(tag);
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
	public class EventListener implements PkoTable.ChangeHandler<Wikivent> {
		@Override public Class<Wikivent> getRecordClass() { return Wikivent.class;}
		@Override public boolean allow(PkoTable<Wikivent>.Change change) { return true;}
		@Override public void rollback(PkoTable<Wikivent>.Change change) {}
		@Override public void commit(PkoTable<Wikivent>.Change change) {
			Wikivent rec = change.newRecord;
			if (rec!=null) {
				if (rec.tags != null) {
					Wikivent oldRec = change.oldRecord;
					if (oldRec == null || !rec.tags.equals(oldRec.tags))
						addTags(change.newRecord.tags);
				}
			}
		}
	}
}




