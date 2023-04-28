package com.spike.spikespringgraphql.cachecontrol;

import graphql.schema.DataFetchingEnvironment;

import java.util.function.Supplier;

public class CacheControlRunner {
    public static <R> R cacheTheResult(DataFetchingEnvironment environment, Integer bookMaxAge, Supplier<R> supplier) throws IllegalAccessException {
        // 手动设置是否 cached。只用在需要的时候设置一次就行。
        // 需要人为保证 non cached 一定手动 put 过。当 object 和 field 能否cache不一致时不好处理。
        environment.getGraphQlContext().put(QueryCachedStatus.QUERY_CACHED, true);
        // 手动设置 cache 时间
        environment.getCacheControl().hint(environment, bookMaxAge);
        // todo: 使用redis cache结果
        // 检查是否是 cached 和 non cached 混合查询
        assetIsMixCachedAndNonCachedQuery(environment);
        return supplier.get();
    }

    public static <R> R nonCacheTheResult(DataFetchingEnvironment environment, Supplier<R> supplier) throws IllegalAccessException {
        // 手动设置是否 cached。只用在需要的时候设置一次就行。
        // 需要人为保证 non cached 一定手动 put 过。当 object 和 field 能否cache不一致时不好处理。
        environment.getGraphQlContext().put(QueryCachedStatus.QUERY_NON_CACHED, true);
        // 检查是否是 cached 和 non cached 混合查询
        assetIsMixCachedAndNonCachedQuery(environment);
        return supplier.get();
    }

    private static void assetIsMixCachedAndNonCachedQuery(DataFetchingEnvironment environment) throws IllegalAccessException {
        if (isMixCachedAndNonCachedQuery(environment)) {
            // todo: 如果是混合查询应该直接抛异常
            // throw new IllegalAccessException(QueryCachedStatus.MIX_CACHED_AND_NON_CACHED_QUERY);
        }
    }

    private static Boolean isMixCachedAndNonCachedQuery(DataFetchingEnvironment environment) {
        Boolean cached = environment.getGraphQlContext().get(QueryCachedStatus.QUERY_CACHED);
        Boolean nonCached = environment.getGraphQlContext().get(QueryCachedStatus.QUERY_NON_CACHED);
        return Boolean.TRUE.equals(cached) && Boolean.TRUE.equals(nonCached);
    }
}
