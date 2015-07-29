package org.kisst.type4j.item;

import org.kisst.type4j.DeeplyImmutable;
import org.kisst.type4j.Immutable;
import org.kisst.type4j.SmartType;
import org.kisst.util.AssertUtil;

public interface SmartItem {
	default public boolean isImmutable() { return this instanceof DeeplyImmutable || this.isDeeplyImmutable(); }
	default public boolean isDeeplyImmutable() { return this instanceof DeeplyImmutable; }

	public class Wrapper implements SmartItem {
		public final Object object;
		public Wrapper(Object object) { this.object=object; AssertUtil.assertNotNull(object); }
		public SmartType<?> getType() { return null; } // TODO:

		public boolean isImmutable() { return object instanceof Immutable || this.isDeeplyImmutable(); }
		public boolean isDeeplyImmutable() { return object instanceof DeeplyImmutable; }
	}
	public class ImmutableWrapper extends Wrapper implements Immutable {
		public ImmutableWrapper(Immutable object) { super(object); }
		public boolean isImmutable() { return true; }
	}
	public class DeeplyImmutableWrapper extends Wrapper implements DeeplyImmutable {
		public DeeplyImmutableWrapper(DeeplyImmutable object) { super(object); }
		public boolean isDeeplyImmutable() { return true; }
	}

}
