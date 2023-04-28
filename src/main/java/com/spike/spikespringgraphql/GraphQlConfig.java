package com.spike.spikespringgraphql;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.preparsed.persisted.ApolloPersistedQuerySupport;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GraphQlConfig {
    @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        ApolloPersistedQuerySupport preparsedDocumentProvider = new ApolloPersistedQuerySupport(new PersistedQueryCache());
        return (builder) -> builder.configureGraphQl(it -> it.preparsedDocumentProvider(preparsedDocumentProvider));
    }

    @Bean
    public Instrumentation maxQueryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(100);
    }

    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(100);
    }
}
