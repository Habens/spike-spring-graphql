package com.spike.spikespringgraphql.bookdetails;

import com.spike.spikespringgraphql.cachecontrol.CacheControlRunner;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BookController {
    @Value("${bff.cache-control.book.max-age}")
    private Integer bookMaxAge;

    @QueryMapping
    public Book bookById(@Argument String id, DataFetchingEnvironment env) throws IllegalAccessException {
        return CacheControlRunner.cacheTheResult(env, bookMaxAge, () -> Book.getById(id));
    }

    @SchemaMapping
    public Author author(Book book, DataFetchingEnvironment env) throws IllegalAccessException {
        return CacheControlRunner.nonCacheTheResult(env, () -> Author.getById(book.getAuthorId()));
    }
}