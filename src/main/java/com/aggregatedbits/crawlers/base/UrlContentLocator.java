package com.aggregatedbits.crawlers.base;

import java.net.URL;
import static com.google.common.base.Preconditions.checkNotNull;


public class UrlContentLocator extends ContentLocator
{
	private static final String DEFAULT_METHOD = "GET";
	private final String method;
	private final URL url;

	public UrlContentLocator( URL url )
	{
		this(url, DEFAULT_METHOD);
	}
	
	public UrlContentLocator( URL url, String method )
	{
		checkNotNull(url);
		checkNotNull(method);
		
		this.url = url;
		this.method = method;
	}

	public URL getUrl( )
	{
		return this.url;
	}
	
	public String getMethod( )
	{
		return this.method;
	}

	@Override
	public String toString( )
	{
		return String.format("{%s: url='%s' method='%s'}", getClass().getSimpleName(), getUrl(), getMethod());
	}

	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object o )
	{
		if( o instanceof UrlContentLocator )
		{
			UrlContentLocator l = (UrlContentLocator) o;
			return getUrl().equals(l.getUrl()) && getMethod().equals(l.getMethod());
		}
		else
		{
			return false;
		}
	}
}
