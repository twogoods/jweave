package com.github.twogoods.jweave.plugin.weave;

import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.plugin.weave.advice.DubboInvokerAdvice;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

@AutoService(TypeInstrumentation.class)
public class DubboOutboundInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.named("org.apache.dubbo.rpc.protocol.dubbo.DubboInvoker");
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("doInvoke"), DubboInvokerAdvice.class.getName())
        };
    }
}