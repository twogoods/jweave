package com.github.twogoods.jweave.agent.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import com.github.twogoods.jweave.agent.core.util.PropertyUtils;
import com.github.twogoods.jweave.agent.core.util.StringUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;

public class LoggerConfigurator extends ContextAwareBase implements Configurator {
    @Override
    public ExecutionStatus configure(LoggerContext loggerContext) {
        this.addInfo("Setting up default configuration.");
        Logger rootLogger = loggerContext.getLogger("ROOT");
        if ("true".equals(PropertyUtils.getProperty("jweave.javaagent.debug", StringUtils.EMPTY))) {
            rootLogger.setLevel(Level.DEBUG);
            Logger grpcLogger = loggerContext.getLogger("com.alibaba.nacos.shaded.io.grpc");
            grpcLogger.setLevel(Level.INFO);
        } else {
            customizeLogLevel(rootLogger);
        }
        startConsoleAppender(rootLogger);
        if ("true".equals(PropertyUtils.getProperty("jweave.log.file", StringUtils.EMPTY))) {
            startRollingFileAppender(rootLogger);
        }
        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }

    private void customizeLogLevel(Logger rootLogger) {
        String logLevel = PropertyUtils.getProperty("jweave.log.level", "INFO");
        rootLogger.setLevel(Level.valueOf(logLevel));
    }

    private void startConsoleAppender(Logger rootLogger) {
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender();
        ca.setContext(this.context);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder();
        encoder.setContext(this.context);
        PatternLayout layout = new PatternLayout();
        layout.setPattern("[jweave.javaagent %d{yyyy-MM-dd HH:mm:ss.SSS Z}] %magenta([%thread]) %highlight(%-5level) %cyan(%logger) %blue(%line) - %msg%n");
        layout.setContext(this.context);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        rootLogger.addAppender(ca);
    }

    private void startRollingFileAppender(Logger rootLogger) {
        String path = PropertyUtils.getProperty("jweave.log.path", "/jweave/log");
        String application = PropertyUtils.getProperty("jweave.service.name", "UNKNOW");
        String maxHistory = PropertyUtils.getProperty("jweave.log.maxHistory", "30");
        String fileSize = PropertyUtils.getProperty("jweave.log.fileSize", "1GB");
        String totalSize = PropertyUtils.getProperty("jweave.log.totalSize", "10GB");

        String filename = String.format("%s-%s-", application, getPid()) + "agent-%d{yyyy-MM-dd}.%i.log";
        if (path.endsWith(File.separator)) {
            path = path + filename;
        } else {
            path = path + "/" + filename;
        }

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(this.context);
        patternLayoutEncoder.setCharset(StandardCharsets.UTF_8);
        patternLayoutEncoder.setPattern("[jweave.javaagent %d{yyyy-MM-dd HH:mm:ss.SSS Z}] %magenta([%thread]) %highlight(%-5level) %cyan(%logger) %blue(%line) - %msg%n");

        RollingFileAppender appender = new RollingFileAppender<PatternLayoutEncoder>();
        appender.setContext(this.context);
        appender.setEncoder(patternLayoutEncoder);
        appender.setName("agentFileAppender");
        appender.setAppend(true);
        appender.setPrudent(true);

        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy<>();
        policy.setContext(this.context);
        policy.setMaxFileSize(FileSize.valueOf(fileSize));
        policy.setFileNamePattern(path);
        policy.setMaxHistory(Integer.parseInt(maxHistory));
        policy.setTotalSizeCap(FileSize.valueOf(totalSize));
        policy.setParent(appender);

        appender.setRollingPolicy(policy);

        patternLayoutEncoder.start();
        policy.start();
        appender.start();
        rootLogger.addAppender(appender);
    }

    private String getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        return name.substring(0, name.indexOf('@'));
    }
}
