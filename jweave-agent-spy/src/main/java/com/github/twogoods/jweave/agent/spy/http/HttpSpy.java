package com.github.twogoods.jweave.agent.spy.http;


import com.github.twogoods.jweave.agent.spy.http.filter.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/4/21
 */
public class HttpSpy {
    public static Map<ClassLoader, HttpInvoker> httpInvokers = new ConcurrentHashMap<>();
    public static Map<String, ClassLoader> providers = new ConcurrentHashMap<>();

    public static void registerInvoker(ClassLoader classLoader, HttpInvoker invoker) {
        httpInvokers.put(classLoader, invoker);
    }

    public static void updateInvoker(ClassLoader classLoader, Object object) {
        HttpInvoker invoker = httpInvokers.get(classLoader);
        if (invoker != null) {
            invoker.update(object);
        }
    }

    public static void registerProvider(String serviceName, ClassLoader classLoader) {
        providers.put(serviceName, classLoader);
    }

    public static boolean hasProvider(String serviceName) {
        return providers.containsKey(serviceName);
    }

    public static HttpResponse invoke(ClassLoader callerCl, HttpRequest request) throws Throwable {
        ClassLoader providerCl = providers.get(request.uri.getAuthority());
        HttpInvoker invoker = httpInvokers.get(providerCl);
        if (invoker != null) {
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(providerCl);
                return invoker.invoke(callerCl, providerCl, request);
            }finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
        throw new RuntimeException("no invoker found");
    }

    public static Object convertRestResponse(ClassLoader callerCl, HttpResponse resp) {
        return httpInvokers.get(callerCl).convertRestResponse(resp);
    }

    private static List<Filter> inboundFilters = new ArrayList<>();
    private static Map<String, Filter> inboundFilterMap = new ConcurrentHashMap<>();

    private static List<Filter> outboundFilters = new ArrayList<>();


    public static void registerInboundFilter(Filter filter) {
        inboundFilterMap.put(filter.name(), filter);
    }

    public static HttpResponse inbound(HttpRequest request) {
        return new FilterExecutor(inboundFilters.iterator(), new HttpExecutor.DefaultHttpExecutor()).execute(request, ExecutionContext.empty());
    }

    public static HttpResponse inbound(HttpRequest request, String filterName) {
        return inboundFilterMap.get(filterName).filter(request, ExecutionContext.empty(), new EmptyFilterExecution());
    }

    public static HttpResponse outbound(HttpRequest request) {
        return new FilterExecutor(outboundFilters.iterator(), new HttpExecutor.DefaultHttpExecutor()).execute(request, ExecutionContext.empty());
    }

}
