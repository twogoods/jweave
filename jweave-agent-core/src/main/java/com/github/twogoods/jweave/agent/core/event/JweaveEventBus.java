package com.github.twogoods.jweave.agent.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JweaveEventBus {

    private static Map<String, List<JweaveSubscriber>> subscribers = new ConcurrentHashMap<>();
    private static Map<String, Object> latestEvent = new ConcurrentHashMap<>();

    public static <T> void pub(String key, T t) {
        latestEvent.put(key, t);
        List<JweaveSubscriber> subs = subscribers.get(key);
        if (subs!= null && !subs.isEmpty()) {
            for (JweaveSubscriber<T> sub : subs) {
                sub.onEvent(t);
            }
        }
    }

    public static <T> void sub(String key, JweaveSubscriber<T> subscriber) {
        subscribers.computeIfAbsent(key, k->new ArrayList<>()).add(subscriber);
        if(latestEvent.containsKey(key)){
            subscriber.onEvent((T) latestEvent.get(key));
        }
    }
}
