package com.aggregatedbits.crawlers.base.events;

public class ContentVisitorBeginEvent<C>
{
    private final C context;

    public ContentVisitorBeginEvent( C context )
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
