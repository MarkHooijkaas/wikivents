package org.kisst.type4j;

public interface DeeplyImmutable extends Immutable { 
	@Override default public boolean isDeeplyImmutable() { return true; }
}
