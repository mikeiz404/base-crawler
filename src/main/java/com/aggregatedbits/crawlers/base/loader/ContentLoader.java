package com.aggregatedbits.crawlers.base.loader;

import com.aggregatedbits.crawlers.base.Content;
import com.aggregatedbits.crawlers.base.context.BaseContextInterface;

public interface ContentLoader<C extends BaseContextInterface<C>>
{
	public Content load( C context ) throws Exception;
}
