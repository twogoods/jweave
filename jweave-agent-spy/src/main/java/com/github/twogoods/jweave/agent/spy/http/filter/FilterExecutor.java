package com.github.twogoods.jweave.agent.spy.http.filter;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;

import java.util.Iterator;

public class FilterExecutor implements FilterExecution{
    private final Iterator<Filter> iterator;
    private HttpExecutor executor;

    public FilterExecutor(Iterator<Filter> iterator, HttpExecutor executor) {
        this.iterator = iterator;
        this.executor = executor;
    }

    @Override
    public HttpResponse execute(HttpRequest request, ExecutionContext context) {
        if (iterator.hasNext()) {
            return iterator.next().filter(request, context, this);
        } else {
            return executor.execute(request, context);
        }
    }
}
