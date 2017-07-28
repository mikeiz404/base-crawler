package com.aggregatedbits.crawlers.base.events;

public class ContentVisitorEndEvent<C>
{
    private final C context;
    private final Exception exception;
    
    public ContentVisitorEndEvent( C context, Exception exception )
    {
        this.context = context;
        this.exception = exception;
    }
    
    public C getContext( )
    {
        return this.context;
    }
    public Exception getException( )
    {
        return this.exception;
    }
    
    @Override
    public String toString( )
    {
        return String.format("{%s: context=%s exception=%s}", getClass().getSimpleName(), getContext(), getException());
    }
}
