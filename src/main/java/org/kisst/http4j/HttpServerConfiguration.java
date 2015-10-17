package org.kisst.http4j;

import org.kisst.props4j.Props;

public class HttpServerConfiguration {

	public final String host;
	public final int port;
	public final long idleTimeout;
	public final String[] restrictedToHost;
	public final boolean redirectToHttps;
	public final boolean sslEnabled;
	public final int sslPort;
	public final String sslKeyStorePath;
	public final String sslKeyStorePassword;
	public final String sslKeyManagerPassword;
	public final String sslTrustStorePath;
	public final String sslTrustStorePassword;

	public HttpServerConfiguration(Props props) {
		this.host = props.getString("host", null);
		this.port = props.getInteger("port", 8080);
		this.idleTimeout = props.getInteger("idleTimeout", 30000);
		this.restrictedToHost = props.getStringArray("restrictedToHost");
		this.redirectToHttps= props.getBoolean("redirectToHttps",false);
		this.sslEnabled = props.getBoolean("sslEnabled");
		this.sslPort = sslEnabled ? props.getInteger("sslPort", 8443) : -1;
		this.sslKeyStorePath = sslEnabled ? props.getString("sslKeyStorePath") : null;
		this.sslKeyStorePassword = sslEnabled ? props.getString("sslKeyStorePassword") : null;
		this.sslKeyManagerPassword = sslEnabled ? props.getString("sslKeyManagerPassword") : null;
		this.sslTrustStorePath = sslEnabled ? props.getString("sslTrustStorePath") : null;
		this.sslTrustStorePassword = sslEnabled ? props.getString("sslTrustStorePassword") : null;
	}

}
