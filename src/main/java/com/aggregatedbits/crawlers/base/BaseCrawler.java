package com.aggregatedbits.crawlers.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aggregatedbits.crawlers.base.context.BaseContextInterface;
import com.aggregatedbits.crawlers.base.events.ContentVisitorEndEvent;
import com.aggregatedbits.crawlers.base.events.CrawlStartEvent;
import com.aggregatedbits.crawlers.base.events.CrawlStopEvent;
import com.aggregatedbits.crawlers.base.events.DiscoveredUnvisitedEvent;
import com.aggregatedbits.crawlers.base.events.DiscoveredVisitedEvent;
import com.aggregatedbits.crawlers.base.loader.ContentLoader;
import com.aggregatedbits.crawlers.base.visitor.ContentVisitor;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


public abstract class BaseCrawler<C extends BaseContextInterface<C>> implements ContentLoader<C>, ContentVisitor<C>
{
    private final Logger logger = LogManager.getLogger();
    
    private final Set<ContentLocator> discoveredLocators = ConcurrentHashMap.newKeySet();
    private ExecutorService locatorExecutor;
    private ExecutorService contentExecutor;
    private final LocatorWorkerFactory<C> locatorWorkerFactory;
    private final ContentWorkerFactory<C> contentWorkerFactory;
    private final AtomicInteger outstandingCount = new AtomicInteger(0);
    private final Object finishedLock = new Object();
    private boolean executorsRunning = false;
    private AtomicBoolean crawlRunning = new AtomicBoolean(false);
    private final int maxActiveConnections;
    private final EventBus events = new EventBus();
    
    public BaseCrawler( int maxActiveConnections )
    {
        checkArgument(maxActiveConnections > 0);
        
        this.maxActiveConnections = maxActiveConnections;
        
        this.locatorWorkerFactory = new LocatorWorkerFactory<>(this.events, this::load);
        this.contentWorkerFactory = new ContentWorkerFactory<>(this.events, this::visit);
        
        this.events.register(this);
    }
    
    public void start( Collection<C> contexts )
    {
        checkNotNull(contexts);
        
        // make sure not other starts can occur
        boolean wasNotRunning = this.crawlRunning.compareAndSet(false, true);
        checkState(wasNotRunning);
        
        // init state
        this.outstandingCount.set(0);
        this.discoveredLocators.clear();
        if( !this.executorsRunning ) startExecutors();
        
        // note: make sure start event occurs before other events such as discovered so log output is cleaner
        logger.info("Crawl started.");
        this.events.post(new CrawlStartEvent<C>(contexts));
        
        logger.debug("Crawl starting with contexts: {}.", contexts);
        contexts.forEach(this::addDiscovered);
    }
    
    public void awaitFinish( ) throws InterruptedException
    {
        if( !this.crawlRunning.get() ) return;
        
        synchronized( this.finishedLock )
        {
            this.finishedLock.wait();
        }
    }
    
    public boolean stop( )
    {
        if( this.executorsRunning ) stopExecutors();
        
        boolean wasRunning = this.crawlRunning.compareAndSet(true, false);
        if( wasRunning )
        {
            logger.info("Crawl stopped.");
            this.events.post(new CrawlStopEvent(CrawlStopReason.REQUESTED, this.outstandingCount.get()));
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isRunning( )
    {
        return this.crawlRunning.get();
    }
    
    public void addDiscovered( C context )
    {
        checkNotNull(context);
        checkState(executorsRunning);
        
        ContentLocator locator = checkNotNull(context.getLocator());
        
        logger.trace("Adding locator to discovered set: {}", locator);
        if( this.discoveredLocators.add(locator) )
        {
            logger.debug("Adding undiscovered locator from context {}.", context);
            this.outstandingCount.incrementAndGet();
            this.locatorExecutor.execute(this.locatorWorkerFactory.makeWorker(context));
            this.events.post(new DiscoveredUnvisitedEvent<C>(context));
        }
        else
        {
            this.events.post(new DiscoveredVisitedEvent<C>(context));
        }
    }
    
    public int getMaxActiveConnections( )
    {
        return this.maxActiveConnections;
    }
    
    protected void startExecutors( )
    {
        checkState(!executorsRunning);
        
        this.executorsRunning = true;
        
        this.locatorExecutor = Executors.newFixedThreadPool(getMaxActiveConnections(), new ThreadFactoryBuilder().setNameFormat("LocatorWorker-%d").build());
        this.contentExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("ContentWorker-%d").build());
        
        locatorWorkerFactory.setContentHandler(( C context ) -> this.contentExecutor.execute(this.contentWorkerFactory.makeWorker(context)));
        contentWorkerFactory.setDiscoveredHandler(this::addDiscovered);
    }
    
    protected void stopExecutors( )
    {
        checkState(executorsRunning);
        
        locatorExecutor.shutdownNow();
        contentExecutor.shutdownNow();
        
        this.executorsRunning = false;
    }
    
    @Subscribe
    protected void onContentWorkerFinished( ContentVisitorEndEvent<C> event )
    {
        int outstanding = this.outstandingCount.decrementAndGet();
        if( outstanding == 0 )
        {
            this.crawlRunning.set(false);
            
            logger.info("Crawl stoped (reason = FINISHED).");
            this.events.post(new CrawlStopEvent(CrawlStopReason.FINISHED, 0));
            
            synchronized( this.finishedLock )
            {
                this.finishedLock.notifyAll();
            }
        }
    }

    public EventBus getEvents( )
    {
        return this.events;
    }
}
