package org.kisst.http4j;

@FunctionalInterface
public interface HttpCallHandler {
	public void handle(HttpCall call, String subPath);
}
