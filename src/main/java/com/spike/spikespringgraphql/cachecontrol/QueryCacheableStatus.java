package com.spike.spikespringgraphql.cachecontrol;

public enum QueryCacheableStatus {
    CACHEABLE_QUERY,
    NON_CACHEABLE_QUERY,
    MIX_QUERY;

    public static final String QUERY_CACHEABLE_CHECK_EXTENSION_KEY = "queryCacheableStatus";
    public static final String MIX_CACHEABLE_AND_NON_CACHEABLE_QUERY = "error: mix cacheable and non cacheable query";
}
