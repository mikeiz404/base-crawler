package com.aggregatedbits.crawlers.base.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;


public class Context implements ContextInterface
{
	private final Map<Object, Object> map = new HashMap<>();

	@Override
	public Object get( Object key )
	{
		return this.map.get(key);
	}

	@Override
	public void set( Object key, Object value )
	{
		this.map.put(key,  value);
	}
	
	@Override
	public void remove( Object key )
	{
		this.map.remove(key);
	}
	
	@Override
	public String toString( )
	{
		StringJoiner joiner = new StringJoiner(", ");
		for( Entry<Object, Object> entry : map.entrySet() )
		{
			joiner.add(String.format("%s=%s", entry.getKey(), entry.getValue()));
		}
		
		return String.format("{%s: %s}", getClass().getSimpleName(), joiner);
	}
}
