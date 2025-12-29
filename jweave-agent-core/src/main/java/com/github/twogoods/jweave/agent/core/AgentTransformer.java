package com.github.twogoods.jweave.agent.core;

import com.github.twogoods.jweave.agent.core.classloader.InstrumentationPluginClassLoader;
import com.github.twogoods.jweave.agent.core.instrumentation.InstrumentationPlugin;
import com.github.twogoods.jweave.agent.core.instrumentation.MethodAdvice;
import com.github.twogoods.jweave.agent.core.instrumentation.TypeInstrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public class AgentTransformer {

    private AgentBuilder agentBuilder;
    private Instrumentation inst;

    public AgentTransformer(Instrumentation inst) {
        this.inst = inst;
        agentBuilder = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .ignore(nameStartsWith("net.bytebuddy"))
                .ignore(nameStartsWith("com.github.twogoods.jweave.agent"))
//                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.Listener() {
                    @Override
                    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {

                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        System.out.println("Transformer error  " + typeName);
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

                    }
                });
    }

    public List<ResettableClassFileTransformer> install(InstrumentationPlugin plugin) {
        List<ResettableClassFileTransformer> resettableClassFileTransformers = new ArrayList<>(plugin.getInstrumentations().size());
        plugin.getInstrumentations().forEach(typeInstrumentation -> {
            ResettableClassFileTransformer resettableClassFileTransformer = agentBuilder.type(typeInstrumentation.typeMatcher())
                    .transform(methodTransformer(typeInstrumentation.methodAdvices(), plugin, typeInstrumentation))
                    .installOn(inst);
            resettableClassFileTransformers.add(resettableClassFileTransformer);
        });
        return resettableClassFileTransformers;
    }

    public AgentBuilder.Transformer methodTransformer(MethodAdvice[] methodAdvices, InstrumentationPlugin plugin, TypeInstrumentation typeInstrumentation) {
        return new CommonTransformer(methodAdvices, plugin, typeInstrumentation);
    }

    static class CommonTransformer implements AgentBuilder.Transformer {
        private TypeInstrumentation typeInstrumentation;
        private MethodAdvice[] methodAdvices;
        private InstrumentationPlugin plugin;
        private Map<ClassLoader, Boolean> prepared = new HashMap<>();

        public CommonTransformer(MethodAdvice[] methodAdvices, InstrumentationPlugin plugin, TypeInstrumentation typeInstrumentation) {
            this.methodAdvices = methodAdvices;
            this.plugin = plugin;
            this.typeInstrumentation = typeInstrumentation;
        }

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
            try {
                InstrumentationPluginClassLoader cl = plugin.getClassLoader();
                cl.addClassLoader(classLoader);
                if (!prepared.containsKey(classLoader)) {
                    typeInstrumentation.prepare(classLoader, cl);
                    prepared.put(classLoader, true);
                }
                for (MethodAdvice methodAdvice : methodAdvices) {
                    Class clazz = cl.loadClass(methodAdvice.getAdviceName());
                    builder = builder.visit(Advice.to(clazz).on(methodAdvice.getMethodMatcher()));
                }
                return builder;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
