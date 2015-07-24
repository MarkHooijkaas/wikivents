package org.kisst.http4j;

import org.kisst.item4j.seq.ItemSequence;
import org.kisst.item4j.struct.Struct;

public interface HttpView {
	public void showViewPage(HttpCall call, Struct data);
	public void showEditPage(HttpCall call, Struct data);
	public void showList(HttpCall call, ItemSequence seq);
}
