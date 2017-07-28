package com.aggregatedbits.crawlers.base;

public interface WorkerFinishedCallback<C>
{
	public void onFinished( C context );
}
