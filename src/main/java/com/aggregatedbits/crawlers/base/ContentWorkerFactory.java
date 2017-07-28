package com.aggregatedbits.crawlers.base;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aggregatedbits.crawlers.base.context.BaseContextInterface;
import com.aggregatedbits.crawlers.base.events.ContentVisitorBeginEvent;
import com.aggregatedbits.crawlers.base.events.ContentVisitorEndEvent;
import com.aggregatedbits.crawlers.base.visitor.ContentVisitor;
import com.google.common.eventbus.EventBus;

public class ContentWorkerFactory<C extends BaseContextInterface<C>>
{
	private final ContentVisitor<C> visitor;
	private Consumer<C> discoveredHandler;
	private final EventBus events;
	
	public ContentWorkerFactory( EventBus events, ContentVisitor<C> visitor )
	{
	    checkNotNull(events);
		checkNotNull(visitor);
		
		this.events = events;
		this.visitor = visitor;
	}
	
	public void setDiscoveredHandler( Consumer<C> handler )
	{
		checkNotNull(handler);
		
		this.discoveredHandler = handler;
	}
	
	public Runnable makeWorker( C context )
	{
		checkNotNull(context);
		
		ContentWorkerFactory<C> that = this;
		return new Runnable( )
		{
			private final Logger logger = LogManager.getLogger();
			
        	@Override
        	public void run( )
        	{
        		// visit
        		logger.trace("Visiting content from context {}.", context);
        		that.events.post(new ContentVisitorBeginEvent<C>(context));
        		Exception exception = null;
				try
				{
					Collection<? extends C> discovereds = visitor.visit(context);
	        		
					// add discovered
	        		logger.debug("Discovered contexts {}.", discovereds);
	        		for( C discovered : discovereds )
	        		{
	        			try
	    				{
	        				discoveredHandler.accept(discovered);
	    				}
	        			catch( Exception e )
	        			{
	        			    // note: this is more of an internal crawler error so it will not generate an event
	        			    // TODO: decide whether or not to fail the entire crawl because of this
	        				logger.error("Handler failed to handle context {}.", discovered);
	        				logger.catching(e);
	        			}
	        		}
				}
        		catch( InterruptedException e )
        		{
        		    exception = e;
        			logger.trace("Visitor interrupted.", e);
        			Thread.currentThread().interrupt();
        		}
				catch( Exception e )
				{
				    exception = e;
					logger.error("Visitor failed to visit {}.", context);
					logger.catching(e);
				}
				finally
				{
					that.events.post(new ContentVisitorEndEvent<C>(context, exception));
				}
        	}
		};
	}
}
