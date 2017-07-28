package com.aggregatedbits.crawlers.base.visitor;

import java.util.Collection;

public interface ContentVisitor<C>
{
	public Collection<C> visit( C context ) throws Exception;
}
