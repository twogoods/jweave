package com.github.twogoods.jweave.plugin.spring;

import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.plugin.spring.rest.ClientHttpRequestAdvice;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

@AutoService(TypeInstrumentation.class)
public class RestOutboundInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.<TypeDescription>named("org.springframework.http.client.AbstractClientHttpRequest");
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("execute"), ClientHttpRequestAdvice.class.getName())
        };
    }
}
