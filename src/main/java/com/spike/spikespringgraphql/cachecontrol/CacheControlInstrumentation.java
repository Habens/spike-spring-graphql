package com.spike.spikespringgraphql.cachecontrol;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CacheControlInstrumentation extends SimpleInstrumentation {
    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {
        ExecutionResult resultWithCacheStatus = ExecutionResultImpl.newExecutionResult()
                .from(executionResult)
                .addExtension(QueryCacheableStatus.QUERY_CACHEABLE_CHECK_EXTENSION_KEY, queryCacheableCheck(parameters.getGraphQLContext()))
                .addExtension(QueryCachedStatus.QUERY_CACHED_STATUS_EXTENSION_KEY, queryCacheStatus(parameters.getGraphQLContext()))
                .build();
        ExecutionResult resultWithCacheControl = parameters.getExecutionInput().getCacheControl().addTo(resultWithCacheStatus);
        return super.instrumentExecutionResult(resultWithCacheControl, parameters);
    }

    private QueryCacheableStatus queryCacheableCheck(GraphQLContext context) {
        Boolean cacheable = context.get(QueryCacheableStatus.CACHEABLE_QUERY.name());
        Boolean nonCacheable = context.get(QueryCacheableStatus.NON_CACHEABLE_QUERY.name());
        if (Boolean.TRUE.equals(cacheable) && Boolean.TRUE.equals(nonCacheable)) {
            return QueryCacheableStatus.MIX_QUERY;
        }
        if (Boolean.TRUE.equals(nonCacheable)) {
            return QueryCacheableStatus.NON_CACHEABLE_QUERY;
        }
        if (Boolean.TRUE.equals(cacheable)) {
            return QueryCacheableStatus.CACHEABLE_QUERY;
        }
        return QueryCacheableStatus.NON_CACHEABLE_QUERY;
    }

    private String queryCacheStatus(GraphQLContext context) {
        Boolean cached = context.get(QueryCachedStatus.QUERY_CACHED);
        Boolean nonCached = context.get(QueryCachedStatus.QUERY_NON_CACHED);
        if (Boolean.TRUE.equals(cached) && Boolean.TRUE.equals(nonCached)) {
            return QueryCachedStatus.MIX_QUERY;
        }
        if (Boolean.TRUE.equals(nonCached)) {
            return QueryCachedStatus.QUERY_NON_CACHED;
        }
        if (Boolean.TRUE.equals(cached)) {
            return QueryCachedStatus.QUERY_CACHED;
        }
        return QueryCachedStatus.QUERY_NON_CACHED;
    }
}
