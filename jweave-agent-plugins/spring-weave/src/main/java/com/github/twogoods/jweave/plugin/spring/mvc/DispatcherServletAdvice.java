package com.github.twogoods.jweave.plugin.spring.mvc;

import com.github.twogoods.jweave.agent.spy.http.HttpSpy;
import net.bytebuddy.asm.Advice;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class DispatcherServletAdvice {

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void constructor(@Advice.This Object obj) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        try {
            HttpSpy.updateInvoker(classLoader, obj);
        } catch (Error e) {
            e.printStackTrace();
        }
    }

}
