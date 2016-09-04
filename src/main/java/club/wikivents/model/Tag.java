package club.wikivents.model;

import org.kisst.item4j.ImmutableSequence;

public class Tag {
	public final TagRepository repo;
	public final String name;

	public Tag(TagRepository repo, String tag) {
		this.repo = repo;
		this.name = tag;
	}

	public ImmutableSequence<User> getUsers() { return repo.getUsers(name);}
	public ImmutableSequence<Event> getEvents() { return repo.getEvents(name);}
}
