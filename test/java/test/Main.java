package test;

import org.kisst.struct4j.HashStruct;

import club.wikivents.model.User;
import club.wikivents.model.WikiventsFileModel;
import club.wikivents.model.WikiventsModel;

public class Main {
	public static void main(String[] args) {
		WikiventsModel model = new WikiventsFileModel("test/data");
		
		HashStruct doc=new HashStruct();
		User.schema.name.setValue(doc, "Mark1967");
		User.schema.email.setValue(doc, "mark@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		model.users().create(new User(doc));
		System.out.println("Added user");

		User.schema.name.setValue(doc, "PKO");
		User.schema.email.setValue(doc, "pko@wikivents.nl");
		User.schema.password.setValue(doc, "secret");
		model.users().create(new User(doc));
		System.out.println("Added user");

	}
}
