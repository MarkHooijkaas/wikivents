package club.wikivents;

import java.io.File;
import java.util.Properties;

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
		File f=new File(configfile);
		if (f.exists())
			props.load(f);
		this.site=new WikiventsSite(props.getPropsOrEmpty("wikivents"));

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
		File log4jConfigfile=new File("config/log4j.properties");
		if (log4jConfigfile.exists())
			PropertyConfigurator.configure(log4jConfigfile.getPath());
		else {
			Properties props = new Properties();
			props.put("log4j.rootLogger", "WARN, stdlog");
			props.put("log4j.appender.stdlog", "org.apache.log4j.ConsoleAppender");
			props.put("log4j.appender.stdlog.target", "System.out");
			props.put("log4j.appender.stdlog.layout", "org.apache.log4j.PatternLayout");
			props.put("log4j.appender.stdlog.layout.ConversionPattern","%d{HH:mm:ss} %-5p %-25c{1} :: %m%n");
			PropertyConfigurator.configure(props);
			logger.warn("Could not find "+log4jConfigfile+" using default settings");
		}
		Runner runner=new Runner("config/wikivents.properties");
		System.out.println("End INIT: "+(System.currentTimeMillis()-ts));
		runner.run();
		System.out.println("SITE stopped");
	}
}
