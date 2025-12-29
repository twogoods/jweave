package com.github.twogoods.jweave.agent.core.instrumentation;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public interface TypeInstrumentation {
    ElementMatcher<TypeDescription> typeMatcher();

    MethodAdvice[] methodAdvices();

    default void prepare(ClassLoader bussinessCl, ClassLoader pluginCl){

    }
}
