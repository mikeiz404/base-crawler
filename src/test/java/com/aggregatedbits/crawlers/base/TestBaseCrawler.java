package com.aggregatedbits.crawlers.base;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;
import com.aggregatedbits.crawlers.base.context.BaseContext;
import com.aggregatedbits.crawlers.base.loader.ContentLoader;
import com.aggregatedbits.crawlers.base.visitor.ContentVisitor;
import static org.mockito.Mockito.*;

public class TestBaseCrawler
{
	public static BaseCrawler<BaseContext> makeCrawler( ContentLoader<BaseContext> loader, ContentVisitor<BaseContext> visitor )
	{
		return new BaseCrawler<BaseContext>(2)
		{
			@Override
			public Content load( BaseContext context ) throws Exception
			{
				return loader.load(context);
			}

			@Override
			public Collection<BaseContext> visit( BaseContext context ) throws Exception
			{
				return visitor.visit(context);
			}
		};
	}
	
	@Test
	public void test( ) throws Exception
	{
		@SuppressWarnings("unchecked")
		ContentLoader<BaseContext> loader = mock(ContentLoader.class);
		@SuppressWarnings("unchecked")
		ContentVisitor<BaseContext> visitor = mock(ContentVisitor.class);
		
		BaseContext a = new BaseContext(new UrlContentLocator(new URL("http://domain.tld/a")));
		BaseContext b = new BaseContext(new UrlContentLocator(new URL("http://domain.tld/b")));
		BaseContext c = new BaseContext(new UrlContentLocator(new URL("http://domain.tld/c")));
		BaseContext d = new BaseContext(new UrlContentLocator(new URL("http://domain.tld/d")));
		BaseContext e = new BaseContext(new UrlContentLocator(new URL("http://domain.tld/e")));
		
		Map<BaseContext, Collection<BaseContext>> graph = new HashMap<>();
		graph.put(a, Arrays.asList(a, b, c));
		graph.put(b, Arrays.asList(c, d, e));
		graph.put(c, Arrays.asList());
		graph.put(d, Arrays.asList());
		graph.put(e, Arrays.asList(a));
		
		Collection<BaseContext> starts = new ArrayList<>();
		starts.add(a);
		starts.add(b);
		
		for( Entry<BaseContext, Collection<BaseContext>> entry : graph.entrySet() )
		{
			BaseContext from = entry.getKey();
			Collection<BaseContext> tos = entry.getValue();
			when(loader.load(from)).thenReturn(new Content(from.getLocator(), new byte[0]));
			when(visitor.visit(from)).thenReturn(tos);
		}
		
		BaseCrawler<BaseContext> crawler = makeCrawler(loader, visitor);
		
		crawler.start(starts);
		crawler.awaitFinish();
		
		for( BaseContext context : graph.keySet() )
		{
			verify(loader, times(1)).load(context);
			verify(visitor, times(1)).visit(context);
		}
	}
}
