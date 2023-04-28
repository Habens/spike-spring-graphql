package com.spike.spikespringgraphql.cachecontrol;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

public abstract class QueryCacheableDirective implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        DataFetcher<?> originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
        DataFetcher<?> newDataFetcher = putQueryCacheableToContext(originalDataFetcher);
        environment.getCodeRegistry().dataFetcher(parentType, field, newDataFetcher);
        return field;
    }

    @Override
    public GraphQLObjectType onObject(SchemaDirectiveWiringEnvironment<GraphQLObjectType> environment) {
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();
        environment.getElement().getFieldDefinitions().forEach(
                it -> {
                    DataFetcher<?> originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, it);
                    DataFetcher<?> newDataFetcher = putQueryCacheableToContext(originalDataFetcher);
                    environment.getCodeRegistry().dataFetcher(parentType, it, newDataFetcher);
                }
        );
        return SchemaDirectiveWiring.super.onObject(environment);
    }

    private DataFetcher<?> putQueryCacheableToContext(DataFetcher<?> originalDataFetcher) {
        // 识别graphql schema 上的 @cacheable 和 @nonCacheable 注解。把对应的值放到 global context
        // 注意：如果一个 object 包含多个 field，这里会放多次！
        return (DataFetcher<?>) DataFetcherFactories.wrapDataFetcher(originalDataFetcher, ((dataFetchingEnvironment, value) -> {
            dataFetchingEnvironment.getGraphQlContext().put(queryCacheable(), true);
            return value;
        }));
    }

    abstract public String queryCacheable();
}
