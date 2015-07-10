package club.wikivents.model;

import java.io.File;

import org.kisst.crud4j.StructStorage;
import org.kisst.crud4j.impl.FileStorage;
import org.kisst.crud4j.impl.MongoStorage;

import com.mongodb.DB;

public class WikiventsModels {
	public static WikiventsModel createFileModel(File maindir) {
		final StructStorage userStorage=new FileStorage(User.schema, maindir);
		final StructStorage eventStorage=new FileStorage(Event.schema, maindir);
		return new WikiventsModel(userStorage,eventStorage);
	}

	public static WikiventsModel createMongoModel(DB db) {
		final StructStorage userStorage=new MongoStorage(User.schema, db);
		final StructStorage eventStorage=new MongoStorage(Event.schema, db);
		return new WikiventsModel(userStorage,eventStorage);
	}
}
