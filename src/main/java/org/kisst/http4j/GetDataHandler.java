package org.kisst.http4j;

import org.kisst.item4j.json.JsonOutputter;

public class GetDataHandler implements HttpCallHandler{
	@FunctionalInterface interface DataProvider {
		public Object getData(HttpCall httpcall);
	}
	
	private final DataProvider provider; 
	private final JsonOutputter outputter=new JsonOutputter("");
	
	public GetDataHandler(DataProvider provider) { this.provider=provider; }
	
	public void handle(HttpCall httpcall, String subPath) {
		Object data = provider.getData(httpcall);
		httpcall.output(outputter.createString(data));
	}
}
