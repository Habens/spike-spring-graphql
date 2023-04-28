package com.spike.spikespringgraphql.cachecontrol;

public class QueryCachedStatus {

    public static final String QUERY_CACHED = "QUERY_CACHED";
    public static final String QUERY_NON_CACHED = "QUERY_NON_CACHED";
    public static final String MIX_QUERY = "MIX_QUERY";

    public static final String QUERY_CACHED_STATUS_EXTENSION_KEY = "queryCachedStatus";
    public static final String MIX_CACHED_AND_NON_CACHED_QUERY = "error: mix cached and non cached query";
}
