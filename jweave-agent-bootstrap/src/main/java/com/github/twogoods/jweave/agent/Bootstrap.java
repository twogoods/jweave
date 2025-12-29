package com.github.twogoods.jweave.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Bootstrap {

    private static final String AGENT_BOOTER = "com.github.twogoods.jweave.agent.core.AgentBooter";

    public static void premain(String args, Instrumentation inst) throws Exception {
        String agentDir = new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        URL url = new File(agentDir + File.separator + "core" + File.separator + "jweave-agent-core.jar").toURI().toURL();
        ClassLoader classLoader = new AgentCoreClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent());
        Class<?> agentBooter = classLoader.loadClass(AGENT_BOOTER);
        agentBooter.getDeclaredMethod("boot", String.class, Instrumentation.class).invoke(null, args, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        premain(agentArgs, instrumentation);
    }


    public static void main(String[] args) throws Exception {
        String apps = System.getProperty("weave.apps");
        if (apps == null) {
            System.out.println("Please set -Dweave.apps=xxx");
            return;
        }
        String[] appPaths = apps.split(",");
        for (String appPath : appPaths) {
            launchJar(appPath);
        }
    }

    public static WeaveApplicationClassLoader launchJar(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Manifest manifest = jarFile.getManifest();
            String mainClass = manifest.getMainAttributes().getValue("Main-Class");
            WeaveApplicationClassLoader bootstrapClassLoader = new WeaveApplicationClassLoader(new URL[]{new File(jarPath).toURL()});
            Class main = bootstrapClassLoader.loadClass(mainClass);
            main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            return bootstrapClassLoader;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
