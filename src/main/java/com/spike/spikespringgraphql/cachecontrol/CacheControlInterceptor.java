package com.spike.spikespringgraphql.cachecontrol;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static graphql.cachecontrol.CacheControl.CACHE_CONTROL_EXTENSION_KEY;

@Component
public class CacheControlInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        Mono<WebGraphQlResponse> next = chain.next(request);
        return next.map(
                it -> {
                    if (QueryCacheableStatus.CACHEABLE_QUERY == queryCacheableStatus((it))) {
                        Integer overallMaxAge = overallMaxAge(it);
                        it.getResponseHeaders().add("cache-control", String.format("max-age=%s", overallMaxAge));
                    }
                    if (QueryCachedStatus.QUERY_CACHED.equals(queryCachedStatus((it)))) { // todo: correct me.
                        Integer overallMaxAge = overallMaxAge(it);
                        it.getResponseHeaders().add("cache-control", String.format("max-age=%s", overallMaxAge));
                    }
                    // todo: hide the hints
                    // it.getExecutionResult().getExtensions().clear();
                    return it;
                }
        );
    }

    private QueryCacheableStatus queryCacheableStatus(WebGraphQlResponse it) {
        return (QueryCacheableStatus) it
                .getExecutionResult()
                .getExtensions()
                .get(QueryCacheableStatus.QUERY_CACHEABLE_CHECK_EXTENSION_KEY);
    }

    private String queryCachedStatus(WebGraphQlResponse it) {
        return (String) it
                .getExecutionResult()
                .getExtensions()
                .get(QueryCachedStatus.QUERY_CACHED_STATUS_EXTENSION_KEY);
    }

    @SuppressWarnings("unchecked")
    private Integer overallMaxAge(WebGraphQlResponse it) {
        Map<String, List<Map<String, Object>>> extensionKey = (Map<String, List<Map<String, Object>>>) it
                .getExecutionResult()
                .getExtensions()
                .get(CACHE_CONTROL_EXTENSION_KEY);
        List<Map<String, Object>> hints = extensionKey.get("hints");
        return hints.stream().map(hint -> (Integer) hint.get("maxAge")).min(Integer::compare).orElse(0);
    }
}

