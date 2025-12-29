package com.github.twogoods.jweave.plugin.spring.mvc;

import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/4/25
 */
public class SpringbootStartAdvice {
    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) ConfigurableEnvironment env) {
        String svcName = env.getProperty("spring.application.name");
        HttpSpy.registerProvider(svcName, env.getClass().getClassLoader());
    }
}
