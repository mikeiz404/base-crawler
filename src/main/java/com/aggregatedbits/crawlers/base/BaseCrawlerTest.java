package com.aggregatedbits.crawlers.base;

import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import com.aggregatedbits.crawlers.base.context.BaseContext;
import com.aggregatedbits.crawlers.base.loader.ContentLoader;
import com.aggregatedbits.crawlers.base.loader.UrlContentLoader;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


public class BaseCrawlerTest extends BaseCrawler<BaseContext>
{

    public BaseCrawlerTest( int maxActiveConnections )
    {
        super(maxActiveConnections);
    }

    ContentLoader<BaseContext> loader = new UrlContentLoader<>();
    
    public static void main( String[] args ) throws Exception
    {
        // note: log4j makes a call to getLocalHostname which takes ~5s for some reason. Calling it here to get delay out
        // of the way.
        LogManager.getLogger();
        
        System.out.println("Press ENTER to continue.");
        System.in.read();
        System.out.println("Running...");
        
        BaseCrawlerTest crawler = new BaseCrawlerTest(2);
        EventBus events = crawler.getEvents();
        
        events.register(new Object()
        {
            @Subscribe
            public void test( DeadEvent event )
            {
                System.out.println("event: " + event.getEvent());
            }
        });
        
        List<BaseContext> starts = new ArrayList<>();
        
        URL url = new URL("http://yahoo.com/?uniqueid=" + (int) (Math.random() * 10));
        BaseContext start = new BaseContext(new UrlContentLocator(url));
        starts.add(start);
        
        crawler.start(starts);
        crawler.awaitFinish();
        
        crawler.start(starts);
        crawler.awaitFinish();
        
        crawler.stop();
        
//        while( true )
//        {
//            System.out.println("starting...");
//            crawler.start(starts);
//            int duration = (int) (Math.random() * 20);
//            System.out.println("sleeping " + duration + "s...");
//            Thread.sleep(Duration.of(duration, ChronoUnit.SECONDS).toMillis());
//            System.out.println("stopping...");
//            crawler.stop();
//        }
    }
    
    @Override
    public Content load( BaseContext context ) throws Exception
    {
        int duration = (int) (1 + Math.random() * 10);
        Thread.sleep(Duration.of(duration, ChronoUnit.SECONDS).toMillis());
        return this.loader.load(context);
    }
    
    @Override
    public Collection<BaseContext> visit( BaseContext context ) throws Exception
    {
        System.out.println("visit context: " + context);
        
        List<BaseContext> discovereds = new ArrayList<>();
        
        URL url = new URL("http://yahoo.com/?uniqueid=" + (int) (Math.random() * 10));
        BaseContext discovered = new BaseContext(new UrlContentLocator(url));
        discovereds.add(discovered);
        
        return discovereds;
    }
}
