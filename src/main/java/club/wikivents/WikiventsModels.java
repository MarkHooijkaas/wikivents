package club.wikivents;

import java.io.File;

import org.kisst.pko4j.impl.FileStorage;
import org.kisst.pko4j.impl.GitStorage;
import org.kisst.props4j.Props;

import club.wikivents.model.Event;
import club.wikivents.model.Group;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;
import club.wikivents.web.WikiventsSite;

public class WikiventsModels {

	private static WikiventsModel createFileModel(WikiventsSite site, Props props) {
		GitStorage git=null;
		if (props.getBoolean("useGit",false)) {
			File datadir = new File(props.getString("datadir", "data"));
			git=new GitStorage(datadir);
		}
		return new WikiventsModel(site,
			new FileStorage<User>(User.schema, props, git),
			new FileStorage<Event>(Event.schema, props, git),
			new FileStorage<Group>(Group.schema, props, git)
		);
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
