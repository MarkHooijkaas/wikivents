package org.kisst.util;

public class CallInfo {
	public String user=null;
	public String action=null;
	public String data=null;
	public void clear() {
		user=null;
		action=null;
		data=null;
	}
	public static final ThreadLocal<CallInfo> instance = new ThreadLocal<CallInfo>();

}
