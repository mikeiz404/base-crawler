package com.aggregatedbits.crawlers.base;

public interface ContentHandler<C>
{
	public void handle( C context, Content content );
}
