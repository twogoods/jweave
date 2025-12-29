package com.github.twogoods.jweave.agent.spy.http.filter;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;

public class EmptyFilterExecution implements FilterExecution {
    public HttpResponse execute(HttpRequest request, ExecutionContext context){
        return new HttpResponse(200, new byte[0]);
    }
}
