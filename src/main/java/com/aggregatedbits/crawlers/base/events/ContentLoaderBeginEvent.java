package com.aggregatedbits.crawlers.base.events;

public class ContentLoaderBeginEvent<C>
{
    private final C context;

    public ContentLoaderBeginEvent( C context )
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
