package club.wikivents.model;

import org.kisst.crud4j.StructStorage;
import org.kisst.crud4j.impl.FileStorage;
import org.kisst.crud4j.impl.MongoStorage;
import org.kisst.item4j.struct.Struct;

public class WikiventsModels {
	public static WikiventsModel createFileModel(Struct props) {
		final StructStorage userStorage=new FileStorage(User.schema, props);
		final StructStorage eventStorage=new FileStorage(Event.schema, props);
		return new WikiventsModel(userStorage,eventStorage);
	}

	public static WikiventsModel createMongoModel(Struct props) {
		final StructStorage userStorage=new MongoStorage(User.schema, props);
		final StructStorage eventStorage=new MongoStorage(Event.schema, props);
		return new WikiventsModel(userStorage,eventStorage);
	}

	public static WikiventsModel createModel(Struct props) {
		String storage = props.getString("storage", "file");
		if ("file".equals(storage))
			return WikiventsModels.createFileModel(props.getStruct("file.storage",Struct.EMPTY));
		else if ("mongo".equals(storage))
			return WikiventsModels.createMongoModel(props.getStruct("mongo.storage",Struct.EMPTY));
		else
			throw new RuntimeException("Unknown storage type "+storage);
	}
}
