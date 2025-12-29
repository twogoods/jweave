package com.github.twogoods.jweave.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class AgentCoreClassLoader extends URLClassLoader {

    public AgentCoreClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
