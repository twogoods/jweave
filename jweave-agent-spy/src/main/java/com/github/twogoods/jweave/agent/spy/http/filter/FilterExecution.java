package com.github.twogoods.jweave.agent.spy.http.filter;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;

public interface FilterExecution {
    HttpResponse execute(HttpRequest request, ExecutionContext context);
}
