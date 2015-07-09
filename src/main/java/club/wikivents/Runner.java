package club.wikivents;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.kisst.props4j.SimpleProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.model.WikiventsFileModel;
import club.wikivents.web.WebSite;

public class Runner {
	final static Logger logger=LoggerFactory.getLogger(Runner.class); 
	private final SimpleProps props=new SimpleProps();
	private final WebSite server;;
	
	public Runner(String configfile) {
		props.load(new File(configfile));
		this.server = new WebSite(new WikiventsFileModel(new File("data")), props);
	}
	public void run() {
		server.startListening();
		server.join();
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/log4j.properties");
		Runner runner=new Runner("config/wikivents.properties");
		long ts=System.currentTimeMillis();
		System.out.println("Starting INIT");
		System.out.println("End INIT: "+(System.currentTimeMillis()-ts));
		runner.run();
		System.out.println("SITE stopped");
	}
}
