package com.aggregatedbits.crawlers.base.events;

public class DiscoveredUnvisitedEvent<C>
{
    private final C context;

    public DiscoveredUnvisitedEvent( C context )
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
