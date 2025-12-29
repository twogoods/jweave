package com.github.twogoods.jweave.agent.core.instrumentation;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class MethodAdvice {
    private ElementMatcher<MethodDescription> methodMatcher;
    private String adviceName;

    private MethodAdvice(ElementMatcher<MethodDescription> methodMatcher, String adviceName) {
        this.methodMatcher = methodMatcher;
        this.adviceName = adviceName;
    }

    public static MethodAdvice of(ElementMatcher<MethodDescription> methodMatcher, String adviceName) {
        return new MethodAdvice(methodMatcher, adviceName);
    }


    public ElementMatcher<MethodDescription> getMethodMatcher() {
        return methodMatcher;
    }

    public String getAdviceName() {
        return adviceName;
    }
}
