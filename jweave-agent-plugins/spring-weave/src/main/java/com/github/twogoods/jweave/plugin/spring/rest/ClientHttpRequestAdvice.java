package com.github.twogoods.jweave.plugin.spring.rest;

import com.github.twogoods.jweave.agent.spy.dubbo.DubboSpy;
import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;
import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class ClientHttpRequestAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean doInvoke(@Advice.This() ClientHttpRequest request) {
        if (HttpSpy.hasProvider(request.getURI().getAuthority())) {
            return true;
        }
        return false;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.This() ClientHttpRequest request,
                            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) ClientHttpResponse result) {
        if (!HttpSpy.hasProvider(request.getURI().getAuthority())) {
            return;
        }
        Map<String, List<String>> header = new HashMap<>();
        header.putAll(request.getHeaders());
        ClientHttpResponse resp = null;
        ClassLoader callerCl = request.getClass().getClassLoader();
        try {
            HttpRequest httpRequest = new HttpRequest(request.getURI(), request.getMethod().name(), header, request.getBody().toString().getBytes());
            HttpResponse response = HttpSpy.invoke(callerCl, httpRequest);
            Object obj = HttpSpy.convertRestResponse(callerCl, response);
            resp = (ClientHttpResponse) obj;
        } catch (Throwable e) {
            e.printStackTrace();
            Object obj = HttpSpy.convertRestResponse(callerCl, new HttpResponse(500, e.getMessage().getBytes()));
            resp = (ClientHttpResponse) obj;
        }
        result = resp;
    }
}
