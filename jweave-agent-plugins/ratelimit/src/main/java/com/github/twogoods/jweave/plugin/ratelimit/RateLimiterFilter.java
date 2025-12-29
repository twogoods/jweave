package com.github.twogoods.jweave.plugin.ratelimit;

import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;
import com.github.twogoods.jweave.agent.spy.http.filter.ExecutionContext;
import com.github.twogoods.jweave.agent.spy.http.filter.Filter;
import com.github.twogoods.jweave.agent.spy.http.filter.FilterExecution;
import org.slf4j.Logger;

public class RateLimiterFilter implements Filter {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(RateLimiterFilter.class);
    @Override
    public String name() {
        return "ratelimit";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public HttpResponse filter(HttpRequest request, ExecutionContext context, FilterExecution execution) {
        LOGGER.debug("RateLimiterFilter execute... ");
        if (RateLimiterController.tryAcquire(request)) {
            return execution.execute(request, context);
        } else {
            return new HttpResponse(429, new byte[0]);
        }
    }
}
