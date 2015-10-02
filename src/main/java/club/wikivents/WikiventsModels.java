package club.wikivents;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.kisst.crud4j.impl.FileStorage;
import org.kisst.crud4j.impl.MongoDb;
import org.kisst.crud4j.impl.MongoStorage;
import org.kisst.crud4j.index.MemoryOrderedIndex;
import org.kisst.crud4j.index.MemoryUniqueIndex;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.StructProps;
import org.kisst.props4j.Props;

import club.wikivents.model.Event;
import club.wikivents.model.Page;
import club.wikivents.model.Group;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;
import club.wikivents.web.WikiventsSite;

public class WikiventsModels {

	private static WikiventsModel createFileModel(WikiventsSite site, Props props) {
		Git git=null;
		try {
			if (props.getBoolean("useGit",false))
				git = Git.open(new File(props.getString("datadir", "data")));
		}
		catch (IOException e) { throw new RuntimeException(e);}
		return new WikiventsModel(site,
			new FileStorage(User.class, props, git),
			new FileStorage(Event.class, props, git),
			new FileStorage(Page.class, props, git),
			new FileStorage(Group.class, props, git),
			new MemoryUniqueIndex<User>(User.schema, true, User.schema.username),
			new MemoryUniqueIndex<User>(User.schema, true, User.schema.email),
			new MemoryUniqueIndex<Page>(Page.schema, true, Page.schema.name),
			new MemoryOrderedIndex<>(Event.schema, false, Event.schema.date, Event.schema._id),
			new MemoryOrderedIndex<>(Event.schema, false, Event.schema._id)
		);
	}

	public static WikiventsModel createMongoModel(WikiventsSite site, Props props) {
		HashStruct defaults= new HashStruct();
		defaults.put("mongodb", "wikivents");
		MongoDb db = new MongoDb(new StructProps(new MultiStruct(props,defaults)), MongoCodecs.options()); 
		WikiventsModel model = new WikiventsModel(site, 
			new MongoStorage(User.schema, props, db),
			new MongoStorage(Event.schema, props, db),
			new MongoStorage(Group.schema, props, db),
			new MemoryUniqueIndex<User>(User.schema, true, User.schema.username),
			new MemoryUniqueIndex<User>(User.schema, true, User.schema.email),
			new MemoryOrderedIndex<>(Event.schema, false, Event.schema.date, Event.schema._id),
			new MemoryOrderedIndex<>(Event.schema, false, Event.schema._id)
		);
		MongoCodecs.setModel(model);
		return model;
	}
	
	public static WikiventsModel createModel(WikiventsSite site, Props props) {
		String storage = props.getString("storage", "file");
		if ("file".equals(storage))
			return WikiventsModels.createFileModel(site, props.getPropsOrEmpty("file.storage"));
	//	else if ("mongo".equals(storage))
		//	return WikiventsModels.createMongoModel(props.getStruct("mongo.storage",Struct.EMPTY));
		else
			throw new RuntimeException("Unknown storage type "+storage);
	}
}
