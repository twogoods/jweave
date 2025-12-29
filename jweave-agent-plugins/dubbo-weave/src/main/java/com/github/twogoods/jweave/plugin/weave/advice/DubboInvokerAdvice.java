package com.github.twogoods.jweave.plugin.weave.advice;

import com.github.twogoods.jweave.agent.spy.dubbo.DubboSpy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;

import java.util.concurrent.CompletableFuture;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboInvokerAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean doInvoke(@Advice.Argument(0) Invocation invocation) {
        if (DubboSpy.hasProvider(invocation.getServiceName())) {
            return true;
        }
        return false;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Argument(0) Invocation invocation,
                            @Advice.Return(readOnly = false,typing = Assigner.Typing.DYNAMIC) Object result) {
        if (!DubboSpy.hasProvider(invocation.getServiceName())) {
            return;
        }
        try {
            ClassLoader classLoader = invocation.getClass().getClassLoader();
            Object obj = DubboSpy.invoke(classLoader, invocation.getServiceName(), invocation.getMethodName(), invocation.getArguments());
            result = new AsyncRpcResult(CompletableFuture.completedFuture(new AppResponse(obj)), invocation);
        } catch (Throwable e) {
            result = new AsyncRpcResult(CompletableFuture.completedFuture(new AppResponse(e)), invocation);
        }
    }
}
