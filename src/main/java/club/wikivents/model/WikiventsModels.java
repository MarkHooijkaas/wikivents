package club.wikivents.model;

import org.kisst.crud4j.impl.FileStorage;
import org.kisst.crud4j.impl.MongoDb;
import org.kisst.crud4j.impl.MongoStorage;
import org.kisst.crud4j.index.MemoryUniqueIndex;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;

public class WikiventsModels {

	private static WikiventsModel createFileModel(Struct props) {
		return new WikiventsModel(
			new FileStorage(User.schema, props),
			new FileStorage(Event.schema, props),
			new MemoryUniqueIndex(User.class, User.schema.username),
			new MemoryUniqueIndex(User.class, User.schema.email)
		);
	}

	public static WikiventsModel createMongoModel(Struct props) {
		HashStruct defaults= new HashStruct();
		defaults.put("mongodb", "wikivents");
		MongoDb db = new MongoDb(new MultiStruct(props,defaults), MongoCodecs.options()); 
		WikiventsModel model = new WikiventsModel(
			new MongoStorage(User.schema, props, db),
			new MongoStorage(Event.schema, props, db)
		);
		MongoCodecs.setModel(model);
		return model;
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
