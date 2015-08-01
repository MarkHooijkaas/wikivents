package test;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.props4j.SimpleProps;

import club.wikivents.WikiventsModels;
import club.wikivents.model.User;
import club.wikivents.model.WikiventsModel;

public class Main {
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getLogger("org.mongodb").setLevel(Level.WARN);
		String configFile="config/test.properties";
		debug("started");

		if (args.length>0)
			configFile=args[0];
		SimpleProps props=new SimpleProps();
		props.load(new File(configFile));


		WikiventsModel model = WikiventsModels.createModel(props.getProps("wikivents"));
		debug("created model");

		for (User u: model.users.findAll())
			System.out.println("\t"+u);
		debug("fetched users");
		
		HashStruct doc=new HashStruct();
		User.schema.username.setValue(doc, "Mark1967");
		User.schema.email.setValue(doc, "mark@wikivents.nl");
		//User.schema.password.setValue(doc, "secret");
		//model.users().create(new User(doc));
		//model.users.create(new User(doc));
		//debug("Added user");

		User.schema.username.setValue(doc, "PKO");
		User.schema.email.setValue(doc, "pko@wikivents.nl");
		//User.schema.password.setValue(doc, "secret");
		//model.users.create(new User(doc));
		//debug("Added user");

		model.close();
		debug("done");
	}

	private static long stopwatch = System.currentTimeMillis();
	private static void debug(String str) {		
		System.out.println((System.currentTimeMillis()-stopwatch)+"\t"+str);
		stopwatch = System.currentTimeMillis();
	}
}
