package test;

import java.io.File;

import org.kisst.struct4j.HashStruct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsFileModel;
import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsMongoModel;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Main {
	public static void main(String[] args) {

		WikiventsModel model = fileModel();
		//WikiventsModel model = mongoModel();

		HashStruct doc=new HashStruct();
		User.schema.username.setValue(doc, "Mark1967");
		User.schema.email.setValue(doc, "mark@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		model.users().create(new User(doc));
		System.out.println("Added user");
		//model.users().create(new User(doc));

		User.schema.username.setValue(doc, "PKO");
		User.schema.email.setValue(doc, "pko@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		model.users().create(new User(doc));
		System.out.println("Added user");

		if (mongoClient!=null)
			mongoClient.close();
	}

	public static WikiventsFileModel fileModel() { return new WikiventsFileModel(new File("test/data")); }
	static MongoClient mongoClient = null;
	public static WikiventsMongoModel mongoModel() {
		mongoClient = new MongoClient("localhost");
		DB db = new DB(mongoClient,"wikivents");
		return new WikiventsMongoModel(db);
	}
}
