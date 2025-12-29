package com.github.twogoods.jweave.agent.core.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class InstrumentationPluginClassLoader extends URLClassLoader {

    private Set<ClassLoader> businessClassLoaders = new HashSet<>();

    public InstrumentationPluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException e) {
            ClassLoader contextCl = Thread.currentThread().getContextClassLoader();
            for (ClassLoader cl : businessClassLoaders) {
                if (classloaderRelated(contextCl, cl)) {
                    try {
                        return cl.loadClass(name);
                    } catch (ClassNotFoundException e1) {
                    }
                }
            }
            for (ClassLoader cl : businessClassLoaders) {
                try {
                    return cl.loadClass(name);
                } catch (ClassNotFoundException e1) {
                }
            }
            throw e;
        }
    }

    private boolean classloaderRelated(ClassLoader contextCl, ClassLoader targetCl) {
        while (contextCl != null) {
            if (contextCl.equals(targetCl)) {
                return true;
            }
            contextCl = contextCl.getParent();
        }
        return false;
    }

    public void addClassLoader(ClassLoader cl) {
        businessClassLoaders.add(cl);
    }
}
