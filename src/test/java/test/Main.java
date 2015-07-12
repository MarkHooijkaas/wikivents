package test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kisst.item4j.struct.HashStruct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;
import club.wikivents.model.WikiventsModels;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Main {
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getLogger("org.mongodb").setLevel(Level.WARN);

		debug("started");

		WikiventsModel model = null;
		if (args.length>0 && args[0].equals("mongo"))
			model = mongoModel();
		else
			model=WikiventsModels.createFileModel(new java.io.File("test/data"));
		debug("created model");

		for (User u: model.users.findAll())
			System.out.println("\t"+u);
		debug("fetched users");
		
		HashStruct doc=new HashStruct();
		User.schema.username.setValue(doc, "Mark1967");
		User.schema.email.setValue(doc, "mark@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		//model.users().create(new User(doc));
		model.users.create(new User(doc));
		debug("Added user");

		User.schema.username.setValue(doc, "PKO");
		User.schema.email.setValue(doc, "pko@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		model.users.create(new User(doc));
		debug("Added user");

		if (mongoClient!=null)
			mongoClient.close();
		debug("done");
	}

	private static long stopwatch = System.currentTimeMillis();
	private static void debug(String str) {		
		System.out.println((System.currentTimeMillis()-stopwatch)+"\t"+str);
		stopwatch = System.currentTimeMillis();
	}
	
	static MongoClient mongoClient = null;
	public static WikiventsModel mongoModel() {
		mongoClient = new MongoClient("localhost");
		DB db = new DB(mongoClient,"wikivents");
		debug("connected to db "+db.getName());
		return WikiventsModels.createMongoModel(db);
	}
}
