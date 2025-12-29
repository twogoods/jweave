package com.github.twogoods.jweave.agent.core.util;

import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PropertyUtils {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(PropertyUtils.class);

    public static String getProperty(String name) {
        return getProperty(name, null);
    }

    public static String getProperty(String name, String defaultVal) {
        String propName = convert2PropName(name);
        String property = System.getProperty(propName);
        if (property == null) {
            String envName = convert2EnvName(name);
            String env = System.getenv(envName);
            if (env != null) {
                property = env;
            }
        }
        if (property == null) {
            property = defaultVal;
        }
        if (LOGGER != null && LOGGER.isTraceEnabled()) {
            LOGGER.trace("Get prop[{}] value: {}", name, property);
        }
        return property;
    }

    private static String convert2PropName(String envName) {
        if (StringUtils.isEmpty(envName)) {
            return envName;
        }
        String[] envNames = envName.split("_");
        List<String> propNames = new ArrayList<>();
        for (String name : envNames) {
            propNames.add(name.toLowerCase());
        }
        return StringUtils.join(propNames, ".");
    }

    private static String convert2EnvName(String propName) {
        if (StringUtils.isEmpty(propName)) {
            return propName;
        }
        String[] propNames = propName.split("\\.");
        List<String> envNames = new ArrayList<>();
        for (String name : propNames) {
            envNames.add(name.toUpperCase());
        }
        return StringUtils.join(envNames, "_");
    }

}
