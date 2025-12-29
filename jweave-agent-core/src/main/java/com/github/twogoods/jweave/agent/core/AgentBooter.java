package com.github.twogoods.jweave.agent.core;

import com.github.twogoods.jweave.agent.core.common.AgentPathFinder;
import com.github.twogoods.jweave.agent.core.common.ConfigManager;
import com.github.twogoods.jweave.agent.core.instrumentation.InstrumentationPluginManager;
import com.github.twogoods.jweave.agent.core.log.AgentLoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.jar.JarFile;

public class AgentBooter {
    private static final Logger LOGGER = AgentLoggerFactory.getLogger(AgentBooter.class);

    public static void boot(String args, Instrumentation inst) {
        String agentPath = AgentPathFinder.findAgentPath();
        installSpy(agentPath, inst);
        List<String> enabledPlugins = ConfigManager.enabledPlugins();
        LOGGER.debug("enabledPlugins:{}", enabledPlugins);
        InstrumentationPluginManager.initPlugins(agentPath, enabledPlugins);
        InstrumentationPluginManager.startBootService();
        AgentTransformer agentTransformer = new AgentTransformer(inst);
        InstrumentationPluginManager.installPlugins(agentTransformer);
    }


    private static void installSpy(String agentPath, Instrumentation inst) {
        File spy = new File(agentPath + File.separator + "core" + File.separator + "jweave-agent-spy.jar");
        try {
            inst.appendToBootstrapClassLoaderSearch(new JarFile(spy));
        } catch (IOException e) {
            LOGGER.error("Append spy jar to system class loader search error", e);
        }
    }
}
