package club.wikivents.model;

import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoTable;

import club.wikivents.web.HasUrl;

public abstract class WikiventsObject<T extends PkoObject> extends BasicPkoObject<WikiventsModel, T> implements HasUrl {
	public WikiventsObject(WikiventsModel model, PkoTable<T> table, Struct data) {
		super(model, table, data);
	}
}
