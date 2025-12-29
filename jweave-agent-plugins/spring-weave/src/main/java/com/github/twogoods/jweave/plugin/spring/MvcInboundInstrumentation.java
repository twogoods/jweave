package com.github.twogoods.jweave.plugin.spring;

import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import com.github.twogoods.jweave.plugin.spring.mvc.DispatcherServletAdvice;
import com.github.twogoods.jweave.plugin.spring.rest.HttpLocalInvoker;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;

import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

@AutoService(TypeInstrumentation.class)
public class MvcInboundInstrumentation implements TypeInstrumentation {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(MvcInboundInstrumentation.class);

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.<TypeDescription>named("org.springframework.web.servlet.FrameworkServlet");
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("setDispatchOptionsRequest").and(takesArguments(1)), DispatcherServletAdvice.class.getName())
        };
    }

    @Override
    public void prepare(ClassLoader bussinessCl, ClassLoader pluginCl) {
        LOGGER.debug("MvcInboundInstrumentation prepare...");
        HttpSpy.registerInvoker(bussinessCl, new HttpLocalInvoker());
    }
}
