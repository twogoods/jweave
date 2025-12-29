package com.github.twogoods.jweave.plugin.ratelimit;

import com.github.twogoods.jweave.agent.core.event.JweaveEventBus;
import com.github.twogoods.jweave.agent.core.event.JweaveSubscriber;
import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;

import static net.bytebuddy.matcher.ElementMatchers.*;

@AutoService(TypeInstrumentation.class)
public class RatelimitInstrumentation implements TypeInstrumentation {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(RatelimitInstrumentation.class);
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.hasSuperType(ElementMatchers.named("javax.servlet.FilterChain"));
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("doFilter").and(takesArgument(0, named("javax.servlet.ServletRequest"))), RateLimitAdvice.class.getName())
        };
    }

    @Override
    public void prepare(ClassLoader bussinessCl, ClassLoader pluginCl) {
        LOGGER.debug("RatelimitInstrumentation prepare...");
        HttpSpy.registerInboundFilter(new RateLimiterFilter());
        JweaveEventBus.sub("ratelimit", (JweaveSubscriber<String>) RateLimiterController::updateConfig);
    }
}
