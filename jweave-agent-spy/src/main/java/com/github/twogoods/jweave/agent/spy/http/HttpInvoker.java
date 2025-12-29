package com.github.twogoods.jweave.agent.spy.http;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public interface HttpInvoker {

    void update(Object dubbo);

    HttpResponse invoke(ClassLoader callerCl, ClassLoader providerCl, HttpRequest request) throws Throwable;

    Object convertRestResponse(HttpResponse resp);

}
