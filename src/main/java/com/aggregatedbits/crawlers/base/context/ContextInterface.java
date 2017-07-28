package com.aggregatedbits.crawlers.base.context;

public interface ContextInterface
{
	public Object get( Object key );
	public void set( Object key, Object value );
	public void remove( Object key );
}
