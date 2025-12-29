package com.github.twogoods.jweave.agent.spy.dubbo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public class DubboSpy {
    public static Map<ClassLoader, DubboInvoker> dubboInvokers = new ConcurrentHashMap<>();
    public static Map<String, ClassLoader> providers = new ConcurrentHashMap<>();

    public static void registerInvoker(ClassLoader classLoader, DubboInvoker invoker) {
        dubboInvokers.putIfAbsent(classLoader, invoker);
    }

    public static void updateInvoker(ClassLoader classLoader, String iface, Object object) {
        providers.put(iface, classLoader);
        DubboInvoker invoker = dubboInvokers.get(classLoader);
        if (invoker != null) {
            invoker.update(object);
        }
    }

    public static boolean hasProvider(String iface) {
        return providers.containsKey(iface);
    }

    public static Object invoke(ClassLoader callerCl, String iface, String methodName, Object[] args) throws Throwable {
        ClassLoader providerCl = providers.get(iface);
        DubboInvoker invoker = dubboInvokers.get(providerCl);
        if (invoker != null) {
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(providerCl);
                return invoker.invoke(callerCl, providerCl, iface, methodName, args);
            }finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
        throw new RuntimeException("no invoker found");
    }
}