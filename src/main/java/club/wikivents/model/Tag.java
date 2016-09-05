package club.wikivents.model;

import java.util.Set;
import java.util.TreeSet;

import org.kisst.item4j.ImmutableSequence;

public class Tag {
	public final TagRepository repo;
	public final Tag parent;
	public final String name;

	public Tag(TagRepository repo, String tag) { this(repo, null, tag);}
	public Tag(TagRepository repo, Tag parent, String tag) {
		this.repo = repo;
		this.parent=parent;
		this.name = tag;
	}

	public Set<Tag> children() { return repo.children(this);}
	public String path() {
		if (parent==null)
			return name;
		return parent.path()+"/"+name;
	}
	public int depth() {
		if (parent==null)
			return 1;
		return parent.depth()+1;
	}

	public ImmutableSequence<User> getUsers() { return repo.getUsers(name);}
	public ImmutableSequence<Event> getEvents() { return repo.getEvents(name);}
}
