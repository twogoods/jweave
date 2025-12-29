package com.github.twogoods.jweave.plugin.ratelimit;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class RateLimiterController {
    private static RateLimiter rateLimiter;

    public static boolean tryAcquire(HttpRequest httpRequest) {
        if (rateLimiter == null) {
            return true;
        }
        //TODO match request, interface level ratelimit
        return rateLimiter.tryAcquire(1);
    }

    public static void updateConfig(String json) {
        JsonObject jsonObject =JsonParser.parseString(json).getAsJsonObject();
        double permitsPerSecond = jsonObject.get("permitsPerSecond").getAsDouble();
        rateLimiter = RateLimiter.create(permitsPerSecond);
    }
}
