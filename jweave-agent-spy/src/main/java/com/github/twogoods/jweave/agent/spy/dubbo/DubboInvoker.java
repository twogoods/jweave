package com.github.twogoods.jweave.agent.spy.dubbo;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public interface DubboInvoker {

    boolean localExist(String iface);

    void update(Object dubbo);

    Object invoke(ClassLoader callerCl, ClassLoader providerCl, String iface, String methodName, Object[] args) throws Throwable;
}
