package club.wikivents;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.kisst.http4j.HttpServer;
import org.kisst.props4j.SimpleProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import club.wikivents.web.WikiventsSite;

public class Runner {
	final static Logger logger=LoggerFactory.getLogger(Runner.class); 
	private final SimpleProps props=new SimpleProps();
	private final HttpServer server;
	private final WikiventsSite site;
	
	public Runner(String configfile) {
		props.load(new File(configfile));
		this.site=new WikiventsSite(props.getProps("wikivents"));
		
		this.server = new HttpServer(props,site);
		
	}
	public void run() {
		server.startListening();
		server.join();
		site.close();
	}
	
	public static void main(String[] args) {
		long ts=System.currentTimeMillis();
		System.out.println("Starting INIT");
		PropertyConfigurator.configure("config/log4j.properties");
		Runner runner=new Runner("config/wikivents.properties");
		System.out.println("End INIT: "+(System.currentTimeMillis()-ts));
		runner.run();
		System.out.println("SITE stopped");
	}
}
