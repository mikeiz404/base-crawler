package com.aggregatedbits.crawlers.base.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import com.aggregatedbits.crawlers.base.Content;
import com.aggregatedbits.crawlers.base.ContentLocator;
import com.aggregatedbits.crawlers.base.UrlContentLocator;
import com.aggregatedbits.crawlers.base.context.BaseContextInterface;
import static com.google.common.base.Preconditions.checkNotNull;

public class UrlContentLoader<C extends BaseContextInterface<C>> implements ContentLoader<C>
{
	public Content load( C context ) throws UnsupportedContentLocatorException, IOException, InterruptedException
	{
		checkNotNull(context);
		ContentLocator locator = checkNotNull(context.getLocator());
		
		if( locator instanceof UrlContentLocator )
		{
			UrlContentLocator urlLocator = (UrlContentLocator) locator;
			HttpURLConnection connection = (HttpURLConnection) urlLocator.getUrl().openConnection();
			connection.setRequestMethod(urlLocator.getMethod());
			ByteArrayOutputStream out = new ByteArrayOutputStream(connection.getContentLength() == -1 ? 1024 : connection.getContentLength());
			
			byte[] buffer = new byte[1024];
			int size;
			while( (size = connection.getInputStream().read(buffer)) != -1 ) out.write(buffer, 0, size);
			
			return new Content(locator, out.toByteArray());
		}
		else
		{
			throw new UnsupportedContentLocatorException(String.format("Locator (%s) is not supported.", locator.getClass()));
		}
	}
}
