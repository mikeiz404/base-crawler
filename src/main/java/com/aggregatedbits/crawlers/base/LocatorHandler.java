package com.aggregatedbits.crawlers.base;

public interface LocatorHandler<C>
{
	public void handle( C context, ContentLocator locator );
}
