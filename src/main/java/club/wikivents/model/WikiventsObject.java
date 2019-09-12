package club.wikivents.model;

import club.wikivents.web.WikiventsCall;
import org.kisst.http4j.handlebar.AccessChecker;
import org.kisst.item4j.struct.Struct;
import org.kisst.pko4j.BasicPkoObject;
import org.kisst.pko4j.PkoObject;
import org.kisst.pko4j.PkoTable;

import club.wikivents.web.HasUrl;

import java.time.Instant;

public abstract class WikiventsObject<T extends PkoObject> extends BasicPkoObject<WikiventsModel, T> implements HasUrl, AccessChecker<User> {
	public WikiventsObject(WikiventsModel model, PkoTable<T> table, Struct data) {
		super(model, table, data);
	}

	public static class Event<T extends PkoObject> {
		private final Instant timestamp=Instant.now();
		private final WikiventsCall callContext;
		public Event(WikiventsCall callContext) {
			this.callContext = callContext;
		}
	}
}
