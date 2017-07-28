package com.aggregatedbits.crawlers.base.context;

import com.aggregatedbits.crawlers.base.Content;
import com.aggregatedbits.crawlers.base.ContentLocator;

public interface BaseContextInterface<C> extends ContextInterface
{
	public enum Key
	{
		LOCATOR,
		CONTENT,
	}
	
	public default ContentLocator getLocator( )
	{
		return (ContentLocator) get(Key.LOCATOR);
	}
	
	public default void setLocator( ContentLocator locator )
	{
		set(Key.LOCATOR, locator);
	}
	
	public default Content getContent( )
	{
		return (Content) get(Key.CONTENT);
	}
	
	public default void setContent( Content locator )
	{
		set(Key.CONTENT, locator);
	}
}
