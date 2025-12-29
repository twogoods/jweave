package com.github.twogoods.jweave.plugin.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.github.twogoods.jweave.agent.core.bootservice.BootService;
import com.github.twogoods.jweave.agent.core.event.JweaveEventBus;
import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import com.github.twogoods.jweave.agent.core.util.PropertyUtils;
import com.google.auto.service.AutoService;
import org.slf4j.Logger;

import java.util.Properties;
import java.util.concurrent.Executor;

@AutoService(BootService.class)
public class NacosConfigService implements BootService {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(NacosConfigService.class);

    @Override
    public String name() {
        return "RateLimiterConfigService";
    }

    @Override
    public void start() {
        LOGGER.debug("NacosConfigService start...");
        /**
         * NacosServiceLoader will use  ServiceLoader.load(clazz), so set Thread ContextClassLoader
         *     public static <S> ServiceLoader<S> load(Class<S> service) {
         *         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         *         return ServiceLoader.load(service, cl);
         *     }
         */
        ClassLoader origin = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            startNacosConfigListener();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(origin);
        }
    }

    @Override
    public void stop() {

    }


    private void startNacosConfigListener() throws Exception {
        Properties properties = new Properties();
        properties.put("serverAddr", PropertyUtils.getProperty("nacos.server", "localhost:8848"));
        String namespace = PropertyUtils.getProperty("nacos.namespace");
        if (namespace != null) {
            properties.put("namespace", namespace);
        }
        ConfigService configService = NacosFactory.createConfigService(properties);
        String dataId = "ratelimit.json";
        String group = PropertyUtils.getProperty("nacos.group", "DEFAULT_GROUP");
        String content = configService.getConfig(dataId, group, 5000);
        if (content != null) {
            JweaveEventBus.pub("ratelimit", content);
        }
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                JweaveEventBus.pub("ratelimit", configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }
}
