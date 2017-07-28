package com.aggregatedbits.crawlers.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.aggregatedbits.crawlers.base.context.BaseContextInterface;
import com.aggregatedbits.crawlers.base.events.ContentLoaderBeginEvent;
import com.aggregatedbits.crawlers.base.events.ContentLoaderEndEvent;
import com.aggregatedbits.crawlers.base.loader.ContentLoader;
import com.google.common.eventbus.EventBus;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.function.Consumer;

public class LocatorWorkerFactory<C extends BaseContextInterface<C>>
{
	private ContentLoader<C> loader;
	private Consumer<C> contentHandler;
	private final EventBus events;
	
	public LocatorWorkerFactory( EventBus events, ContentLoader<C> loader )
	{
	    checkNotNull(events);
		checkNotNull(loader);
		
		this.events = events;
		this.loader = loader;
	}

	public void setContentHandler( Consumer<C> handler )
	{
		checkNotNull(handler);
		
		this.contentHandler = handler;
	}

	public Runnable makeWorker( C context )
	{
		checkNotNull(context);
		checkState(contentHandler != null);
		
		LocatorWorkerFactory<C> that = this;
		
		return new Runnable( )
		{
			private final Logger logger = LogManager.getLogger();
			
        	@Override
        	public void run( )
        	{
        		logger.trace("Running.");
        		that.events.post(new ContentLoaderBeginEvent<C>(context));
        		Exception exception = null;
        		try
        		{
        			logger.debug("Loading content from context {}.", context);
        			Content content = loader.load(context);
        			context.setContent(content);
        			logger.trace("Loaded content {}.", content);
        			
        			contentHandler.accept(context);
        		}
        		catch( InterruptedException e )
        		{
        		    exception = e;
        			logger.trace("Loader interrupted.", e);
        			Thread.currentThread().interrupt();
        		}
        		catch( Exception e )
        		{
        		    exception = e;
        			logger.error("Loader failed to load from context {}", context);
        			logger.catching(e);
        		}
        		finally
        		{
        		    that.events.post(new ContentLoaderEndEvent<C>(context, exception));
        		}
        	}
		};
	}
}
