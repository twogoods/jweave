package com.github.twogoods.jweave.agent.core.instrumentation;

import com.github.twogoods.jweave.agent.core.bootservice.BootService;
import com.github.twogoods.jweave.agent.core.classloader.InstrumentationPluginClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class InstrumentationPlugin {
    private String name;
    private URL path;

    private InstrumentationPluginClassLoader classLoader;
    private List<TypeInstrumentation> instrumentations = new ArrayList<>();
    private List<BootService> bootServices = new ArrayList<>();

    public InstrumentationPlugin(String name, URL path) {
        this.name = name;
        this.path = path;
    }

    public void loadInstrumentations(ClassLoader parentClassLoader) {
        if (classLoader == null) {
            this.classLoader = new InstrumentationPluginClassLoader(new URL[]{path}, parentClassLoader);
        }
        Iterable<? extends TypeInstrumentation> typeInstrumentations = ServiceLoader.load(TypeInstrumentation.class, classLoader);
        for (TypeInstrumentation instrumentation : typeInstrumentations) {
            instrumentations.add(instrumentation);
        }
    }

    public void loadBootServices(ClassLoader parentClassLoader) {
        if (classLoader == null) {
            this.classLoader = new InstrumentationPluginClassLoader(new URL[]{path}, parentClassLoader);
        }
        Iterable<? extends BootService> bootSvcs = ServiceLoader.load(BootService.class, classLoader);
        for (BootService bs : bootSvcs) {
            //TODO order list by priority()
            bootServices.add(bs);
        }
    }

    public void startBootServices() {
        for (BootService bs : bootServices) {
            bs.start();
        }
    }

    public List<TypeInstrumentation> getInstrumentations() {
        return instrumentations;
    }


    public InstrumentationPluginClassLoader getClassLoader() {
        return classLoader;
    }

    public void addBussinessClassLoader(ClassLoader cl) {
        classLoader.addClassLoader(cl);
    }
}
