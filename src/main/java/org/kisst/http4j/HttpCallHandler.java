package org.kisst.http4j;

public interface HttpCallHandler<T extends HttpCall> {
	public void handle(T call, String subPath);
}
