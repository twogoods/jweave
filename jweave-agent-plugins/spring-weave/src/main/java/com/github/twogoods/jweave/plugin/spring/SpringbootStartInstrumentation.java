package com.github.twogoods.jweave.plugin.spring;

import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.plugin.spring.mvc.LoadOnStartupAdvice;
import com.github.twogoods.jweave.plugin.spring.mvc.SpringbootStartAdvice;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

@AutoService(TypeInstrumentation.class)
public class SpringbootStartInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.<TypeDescription>named("org.springframework.boot.SpringApplication");
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("prepareEnvironment"), SpringbootStartAdvice.class.getName())
        };
    }
}
