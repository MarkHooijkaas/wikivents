package test;

import java.io.File;

import org.kisst.item4j.struct.HashStruct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Main {
	public static void main(String[] args) {

		WikiventsModel model = WikiventsModels.createFileModel(new File("test/data"));
		//WikiventsModel model = mongoModel();

		for (User u: model.users.findAll())
			System.out.println("***"+u);
		
		HashStruct doc=new HashStruct();
		User.schema.username.setValue(doc, "Mark1967");
		User.schema.email.setValue(doc, "mark@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		//model.users().create(new User(doc));
		System.out.println("Added user");
		//model.users().create(new User(doc));

		User.schema.username.setValue(doc, "PKO");
		User.schema.email.setValue(doc, "pko@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		//model.users().create(new User(doc));
		System.out.println("Added user");

		if (mongoClient!=null)
			mongoClient.close();
	}

	
	static MongoClient mongoClient = null;
	public static WikiventsModel mongoModel() {
		mongoClient = new MongoClient("localhost");
		DB db = new DB(mongoClient,"wikivents");
		return WikiventsModels.createMongoModel(db);
	}
}
