package com.github.twogoods.jweave.agent.spy.http.filter;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;

public interface Filter {

    String name();

    int order();

    HttpResponse filter(HttpRequest request, ExecutionContext context, FilterExecution execution);
}
