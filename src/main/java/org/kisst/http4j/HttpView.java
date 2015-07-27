package org.kisst.http4j;

import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.Struct;

public interface HttpView {
	public void render(HttpCall call, Struct data);
	public void validateCreate(String userid, HashStruct data);
	public void validateUpdate(String userid, Struct oldRecord, Struct newRecord);
}
