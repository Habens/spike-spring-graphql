## spike spring-graphql

* @cacheable @nonCacheable 控制query的合法性（自动），会有重复设置的性能损耗
* QUERY_CACHED QUERY_NON_CACHED 标记是否真的cache数据（手动），不方便，且不好识别是全部cached还是部分cached