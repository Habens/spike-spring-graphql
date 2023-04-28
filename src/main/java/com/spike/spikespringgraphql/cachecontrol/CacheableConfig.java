package com.spike.spikespringgraphql.cachecontrol;

import graphql.schema.idl.RuntimeWiring.Builder;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Component;

@Component
public class CacheableConfig implements RuntimeWiringConfigurer {

    @Override
    public void configure(Builder builder) {
        builder.directive("cacheable", new QueryCacheableDirective() {
            @Override
            public String queryCacheable() {
                return QueryCacheableStatus.CACHEABLE_QUERY.name();
            }
        });

        builder.directive("nonCacheable", new QueryCacheableDirective() {
            @Override
            public String queryCacheable() {
                return QueryCacheableStatus.NON_CACHEABLE_QUERY.name();
            }
        });
    }
}
