package com.spike.spikespringgraphql;

import graphql.ExecutionInput;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.persisted.PersistedQueryCacheMiss;
import graphql.execution.preparsed.persisted.PersistedQueryNotFound;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

@Component
public class PersistedQueryCache implements graphql.execution.preparsed.persisted.PersistedQueryCache {
    private static final Cache cache = new ConcurrentMapCache("persistedQueryCache");

    @Override
    public PreparsedDocumentEntry getPersistedQueryDocument(Object persistedQueryId, ExecutionInput executionInput, PersistedQueryCacheMiss onCacheMiss) throws PersistedQueryNotFound {

        final String cacheKey = getCacheKey(persistedQueryId, executionInput.getOperationName());
        return cache.get(cacheKey, () -> cache.get(cacheKey, () -> {
            final String queryText = executionInput.getQuery();
            return onCacheMiss.apply(queryText);
        }));
    }

    /**
     * cache key 前面加上operationName，避免意外的hash冲突.
     */
    private String getCacheKey(Object persistedQueryId, String operationName) {
        return String.format("%s:%s", operationName, persistedQueryId);
    }
}
