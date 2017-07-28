package com.aggregatedbits.crawlers.base.events;

public class DiscoveredVisitedEvent<C>
{
    private final C context;

    public DiscoveredVisitedEvent( C context )
    {
        this.context = context;
    }

    public C getContext( )
    {
        return this.context;
    }
    
    @Override
    public String toString( )
    {
        return String.format("{%s: context=%s}", getClass().getSimpleName(), getContext());
    }
}
