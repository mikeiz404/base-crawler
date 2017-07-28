package com.aggregatedbits.crawlers.base;

import static com.google.common.base.Preconditions.checkNotNull;

public class Content
{
	private final ContentLocator source;
	private final byte[] data;
	
	public Content( ContentLocator source, byte[] data )
	{
		checkNotNull(source);
		checkNotNull(data);
		
		this.source = source;
		this.data = data;
	}

	public ContentLocator getSource( )
	{
		return source;
	}

	public byte[] getData( )
	{
		return data;
	}

	@Override
	public String toString( )
	{
		return String.format("{Content: source=%s, data.length=%sB}", source, data.length);
	}
}
