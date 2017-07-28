package com.aggregatedbits.crawlers.base.context;

import com.aggregatedbits.crawlers.base.ContentLocator;

public class BaseContext extends Context implements BaseContextInterface<BaseContext>
{
	public BaseContext( ContentLocator locator )
	{
		this.setLocator(locator);
	}
}
