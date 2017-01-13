package club.wikivents.model;

import org.kisst.item4j.ImmutableSequence;

import com.github.jknack.handlebars.Handlebars.SafeString;

public class Tag {
	public final TagRepository repo;
	public final String name;
	public String childs =",";
	public String parents=",";

	public Tag(TagRepository repo, String tag) {
		this.repo = repo;
		this.name = tag;
	}

	public void addParent(String tag) { parents = addTag(parents, tag);}
	public void addChild(String tag) { childs = addTag(childs, tag);}
	public boolean isParent() { return childs.length()>1;}
	public boolean isChild() { return parents.length()>1;}
	public int getWeight() { return 100 + childs.length()*10 - parents.length();}
	public int getPopularity() { return nrofEvents()*1000 + nrofUsers()*1000 + getWeight(); }

	public SafeString link() {
		return new SafeString("<a href=\"/tag/" + name + "\">" + name + "</a>");
	}

	public Tag[] parentTags() { return repo.tagList(parents);}
	public Tag[] childTags() { return repo.tagList(childs);}

	private String addTag(String list, String tag){
		tag=tag.trim().toLowerCase();
		if (contains(list,tag))
			return list;
		if (list.length() == 0)
			return tag;
		return list+tag+",";
	}

	@Override public String toString() { return name; }
	private boolean contains(String list, String tag) { return list.indexOf(","+tag.trim()+",")>=0; }
	public ImmutableSequence<User> getUsers() { return repo.getUsers(name);}
	public ImmutableSequence<Event> getEvents() { return repo.getEvents(name);}
	public int nrofUsers() {
		ImmutableSequence<User> list = getUsers();
		if (list==null)
			return 0;
		return list.size();
	}
	public int nrofEvents() {
		ImmutableSequence<Event> list = getEvents();
		if (list==null)
			return 0;
		return list.size();
	}

	public static String normalize(String tag) {
		tag=tag.toLowerCase().trim();
		tag=tag.replaceAll("\\s+", "-").replaceAll("[^-a-zA-Z0-9]", "").replaceAll("\\-\\-+", "-");
		return tag;
	}
}
