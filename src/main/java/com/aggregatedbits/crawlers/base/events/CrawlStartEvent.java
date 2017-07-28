package com.aggregatedbits.crawlers.base.events;

import java.util.Collection;

public class CrawlStartEvent<C>
{
    private final Collection<C> contexts;

    public CrawlStartEvent( Collection<C> contexts )
    {
        this.contexts = contexts;
    }

    public Collection<C> getContexts( )
    {
        return this.contexts;
    }
    
    @Override
    public String toString( )
    {
        return String.format("{%s: contexts=%s}", getClass().getSimpleName(), getContexts());
    }
}
