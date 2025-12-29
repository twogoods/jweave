package com.github.twogoods.jweave.plugin.spring.rest;

import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.spy.http.HttpInvoker;
import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;
import com.github.twogoods.jweave.plugin.spring.mvc.mock.MockHttpServletRequest;
import com.github.twogoods.jweave.plugin.spring.mvc.mock.MockHttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class HttpLocalInvoker implements HttpInvoker {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(HttpLocalInvoker.class);
    private DispatcherServlet dispatcherServlet;
    private Method method;

    public void update(Object dispatcherServlet) {
        if (dispatcherServlet instanceof DispatcherServlet) {
            this.dispatcherServlet = (DispatcherServlet) dispatcherServlet;
        }
    }

    public void initReflectCall() {
        if (method != null) {
            return;
        }
        try {
            method = DispatcherServlet.class.getDeclaredMethod("doService", HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            dispatcherServlet.init();
        } catch (Exception e) {
            LOGGER.error("DispatcherServlet init call error", e);
        }
    }

    @Override
    public HttpResponse invoke(ClassLoader callerCl, ClassLoader providerCl, HttpRequest request) throws Throwable {
        LOGGER.debug("Http local invoke...");
        initReflectCall();
        MockHttpServletRequest hsr = new MockHttpServletRequest(request.method, request.uri.getPath());
        request.headers.forEach((k, list) -> list.forEach(v -> hsr.addHeader(k, v)));
        hsr.setContent(request.body);
        MockHttpServletResponse hsrsp = new MockHttpServletResponse();
        try {
            method.invoke(dispatcherServlet, hsr, hsrsp);
        } catch (Exception e) {
            LOGGER.error("DispatcherServlet doService call error", e);
        }
        Map<String, List<String>> respHeaders = new HashMap<>();
        for (String name : hsrsp.getHeaderNames()) {
            respHeaders.put(name, hsrsp.getHeaders(name));
        }
        HttpResponse resp = new HttpResponse(respHeaders, hsrsp.getStatus(), hsrsp.getContentAsByteArray());
        return resp;
    }

    @Override
    public Object convertRestResponse(HttpResponse resp) {
        HttpHeaders headers = new HttpHeaders();
        resp.headers.forEach(headers::addAll);
        return new SpringHttpResponse(resp.code, "", headers, resp.body);
    }
}
