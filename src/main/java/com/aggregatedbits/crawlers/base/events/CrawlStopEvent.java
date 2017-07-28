package com.aggregatedbits.crawlers.base.events;

import com.aggregatedbits.crawlers.base.CrawlStopReason;

public class CrawlStopEvent
{
    private final CrawlStopReason reason;
    private final int outstandingCount;

    public CrawlStopEvent( CrawlStopReason reason, int outstandingCount )
    {
        this.reason = reason;
        this.outstandingCount = outstandingCount;
    }

    public CrawlStopReason getReason( )
    {
        return this.reason;
    }
    
    public int getOutstandingCount( )
    {
        return this.outstandingCount;
    }
    
    @Override
    public String toString( )
    {
        return String.format("{%s: reason=%s oustandingCount=%s}", getClass().getSimpleName(), getReason(), getOutstandingCount());
    }
}
