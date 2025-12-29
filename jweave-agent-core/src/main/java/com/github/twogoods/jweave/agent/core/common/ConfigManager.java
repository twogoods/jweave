package com.github.twogoods.jweave.agent.core.common;

import com.github.twogoods.jweave.agent.core.util.PropertyUtils;

import java.util.Arrays;
import java.util.List;

public class ConfigManager {

    public static List<String> enabledPlugins() {
        String ps = PropertyUtils.getProperty("jweave.enabled.plugins", "dubbo-weave,spring-weave,ratelimit,nacos-config-service");
        return Arrays.asList(ps.split(","));
    }
}