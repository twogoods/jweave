package com.github.twogoods.jweave.agent.core.common;

import java.io.File;

public class AgentPathFinder {
    public static String findAgentPath() {
        String agentPath = new File(AgentPathFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getParent();
        return agentPath;
    }
}
