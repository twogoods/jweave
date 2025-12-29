package com.github.twogoods.jweave.plugin.ratelimit;

import com.github.twogoods.jweave.agent.spy.http.HttpRequest;
import com.github.twogoods.jweave.agent.spy.http.HttpResponse;
import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import net.bytebuddy.asm.Advice;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;

public class RateLimitAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void enter(@Advice.Argument(0) ServletRequest req, @Advice.Argument(1) ServletResponse resp) {
        if (req.getAttribute("ratelimit") != null) {
            return;
        }
        req.setAttribute("ratelimit", Boolean.TRUE);
        HttpServletRequest request = (HttpServletRequest) req;
        HttpRequest httpRequest = new HttpRequest(URI.create(request.getRequestURI()), request.getMethod(), new HashMap<>(), new byte[]{});
        HttpResponse response = HttpSpy.inbound(httpRequest, "ratelimit");
        if (response.code == 429) {
            ((HttpServletResponse) resp).setStatus(429);
        }
    }

}
