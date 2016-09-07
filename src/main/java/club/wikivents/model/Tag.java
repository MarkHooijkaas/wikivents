package club.wikivents.model;

import java.util.Set;
import java.util.TreeSet;

import org.kisst.item4j.ImmutableSequence;

import com.github.jknack.handlebars.Handlebars.SafeString;

public class Tag {
	public final TagRepository repo;
	public final String name;
	public String elements="";
	public String types="";

	public Tag(TagRepository repo, String tag) {
		this.repo = repo;
		this.name = tag;
	}

	public void addType(String tag) { types = addTag(types, tag);}
	public void addElement(String tag) { elements = addTag(elements, tag);}
	public boolean isType() { return elements.length()>0;}

	public SafeString link() {
		return new SafeString("<a href=\"/tag/" + name + "\">" + name + "</a>");
	}

	public Tag[] typeTags() { return repo.tagList(getType());}
	public Tag[] elementTags() { return repo.tagList(elements);}

	public String getType() {
		if (types.length()>0)
			return types;
		if (isType())
			return "categorie";
		return "zonder-categorie";
	}

	private String addTag(String list, String tag){
		if (contains(list,tag))
			return list;
		if (list.length() == 0)
			return tag;
		return list+","+tag;
	}

	@Override public String toString() { return name; }
	private boolean contains(String list, String tag) { return (","+list+",").indexOf(","+tag.trim()+",")>=0; }
	public ImmutableSequence<User> getUsers() { return repo.getUsers(name);}
	public ImmutableSequence<Event> getEvents() { return repo.getEvents(name);}
}
