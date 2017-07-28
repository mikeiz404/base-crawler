package com.aggregatedbits.crawlers.base;

public abstract class ContentLocator
{
	@Override
	public String toString( )
	{
		return String.format("{%s}", getClass().getSimpleName());
	}
	
}
