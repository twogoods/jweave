package com.github.twogoods.jweave.plugin.weave;

import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.spy.dubbo.DubboSpy;
import com.github.twogoods.jweave.plugin.weave.advice.DubboLocalInvoker;
import com.github.twogoods.jweave.plugin.weave.advice.DubboProtocolAdvice;
import com.google.auto.service.AutoService;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;

import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

@AutoService(TypeInstrumentation.class)
public class DubboInboundInstrumentation implements TypeInstrumentation {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(DubboInboundInstrumentation.class);
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.named("org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol");
    }

    @Override
    public MethodAdvice[] methodAdvices() {
        return new MethodAdvice[]{
                MethodAdvice.of(ElementMatchers.named("export").and(takesArguments(1)), DubboProtocolAdvice.class.getName())
        };
    }

    @Override
    public void prepare(ClassLoader bussinessCl, ClassLoader pluginCl) {
        LOGGER.debug("DubboInboundInstrumentation prepare...");
        DubboSpy.registerInvoker(bussinessCl, new DubboLocalInvoker());
    }
}