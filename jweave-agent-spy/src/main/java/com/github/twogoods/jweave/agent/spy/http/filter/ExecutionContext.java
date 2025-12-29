package com.github.twogoods.jweave.agent.spy.http.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExecutionContext {
    private final Map<String, Object> attribute = new HashMap<>();

    public void setAttribute(String key, Object value) {
        if (value == null) {
            this.attribute.remove(key);
        }
        this.attribute.put(key, value);
    }

    public <T> Optional<T> getAttribute(String key, Class<T> type) {
        Object value = this.attribute.get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        } else {
            return Optional.empty();
        }
    }

    public static ExecutionContext empty() {
        return new ExecutionContext();
    }

    public static class Attributes {
    }

}
