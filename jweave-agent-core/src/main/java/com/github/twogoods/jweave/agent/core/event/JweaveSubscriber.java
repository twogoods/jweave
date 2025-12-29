package com.github.twogoods.jweave.agent.core.event;

public interface JweaveSubscriber<T> {
    void onEvent(T t);
}
