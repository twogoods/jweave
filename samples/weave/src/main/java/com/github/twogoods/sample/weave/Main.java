package com.github.twogoods.sample.weave;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Main {
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        List<String> jars = new ArrayList<>();
        String provider = "/Users/twogoods/Documents/code/jweave/samples/provider/target/provider.jar";
        String consumer = "/Users/twogoods/Documents/code/jweave/samples/consumer/target/consumer.jar";
        System.out.println(provider);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                launchJar(provider);
            }
        });
        Thread.sleep(10000);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                launchJar(consumer);
            }
        });
    }

    public static WaeverAgentBootstrapClassLoader launchJar(String jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            Manifest manifest = jarFile.getManifest();
            String mainClass = manifest.getMainAttributes().getValue("Main-Class");

            WaeverAgentBootstrapClassLoader bootstrapClassLoader = new WaeverAgentBootstrapClassLoader(new URL[]{new File(jarPath).toURL()});
            Class main = bootstrapClassLoader.loadClass(mainClass);
            main.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
            return bootstrapClassLoader;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class WaeverAgentBootstrapClassLoader extends URLClassLoader {
        public WaeverAgentBootstrapClassLoader(URL[] urls) {
            super(urls);
        }
    }
}
